package com.prvz.url_checker.service

import com.prvz.url_checker.CheckTask
import com.prvz.url_checker.CheckTaskHistory
import com.prvz.url_checker.exception.IllegalTaskOperationException
import com.prvz.url_checker.exception.NoTasksException
import com.prvz.url_checker.exception.TaskAlreadyClosedException
import com.prvz.url_checker.repository.CheckTaskHistoryRepository
import com.prvz.url_checker.repository.CheckTaskRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.time.OffsetDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

@Service
open class TaskService(
        private val taskRepository: CheckTaskRepository,
        private val taskHistoryRepository: CheckTaskHistoryRepository
) {

    @Suppress("LeakingThis")
    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    private val taskService: ScheduledExecutorService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors())

    private val currentTasks: MutableList<ScheduledFuture<*>> = mutableListOf()
    private val mainLock = ReentrantLock()

    init {
        val lastTask = taskRepository.findTopByOrderByCreateDate()
        if (lastTask != null && lastTask.closeDate == null) {
            runCheckTask(lastTask)
        }
    }

    /**
     * Получение новой таски на исполнение.
     * Сохраняет новую таску в базу,
     * приостанавливает старую таску,
     * запускает новую таску
     */
    fun newTask(checkTask: CheckTask): CheckTask {
        mainLock.lock()
        val lastTask = taskRepository.findTopByOrderByCreateDate()
        if (lastTask != null) {
            closeAndCancelTask(lastTask)
        }
        val task = taskRepository.save(checkTask)
        runCheckTask(task)
        mainLock.unlock()
        return task
    }

    fun stopTask(id: String) {
        mainLock.lock()
        try {
            taskRepository.findTopByOrderByCreateDate()
                    ?.let {
                        if (it.id == id) {
                            if (it.closeDate != null) {
                                throw TaskAlreadyClosedException(id)
                            }
                            closeAndCancelTask(it)
                        } else {
                            throw IllegalTaskOperationException(id,
                                    "Идентификатор задачи для остановки не соответствует данному")
                        }
                    }
                    ?: throw NoTasksException()
        } finally {
            mainLock.unlock()
        }
    }

    fun stopLast() {
        mainLock.lock()
        try {
            taskRepository.findTopByOrderByCreateDate()
                    ?.let {
                        if (it.closeDate != null) {
                            throw TaskAlreadyClosedException(it.id!!)
                        }
                        closeAndCancelTask(it)
                    }
                    ?: throw NoTasksException()
        } finally {
            mainLock.unlock()
        }
    }

    fun closeAndCancelTask(task: CheckTask) {
        taskRepository.save(task.copy(closeDate = OffsetDateTime.now()))
        cancelCurrentTasks()
    }

    fun cancelCurrentTasks() {
        if (currentTasks.isEmpty()) return
        var done: Boolean
        currentTasks.forEach { f -> f.cancel(false) }
        do {
            done = currentTasks.filter { f -> f.isDone }.count() == currentTasks.size
        } while (!done)
        currentTasks.clear()
    }

    fun getTaskInfo(id: String): CheckTask? =
            taskRepository.findById(id).orElse(null)

    fun getLastInfo(): CheckTask? =
            taskRepository.findTopByOrderByCreateDate()

    fun getTaskHistory(id: String, pageable: Pageable): Page<CheckTaskHistory> =
            taskHistoryRepository.findAllByTaskId(id, pageable)

    fun getLastHistory(pageable: Pageable): Page<CheckTaskHistory> =
            taskRepository.findTopByOrderByCreateDate()
                    ?.let { taskHistoryRepository.findAllByTaskId(it.id!!, pageable) }
                    ?: PageImpl<CheckTaskHistory>(emptyList())

    private fun runCheckTask(checkTask: CheckTask) {
        checkTask.urls.forEach { url ->
            try {
                val parsedUrl = URL(
                        if (url.startsWith("https"))
                            url.replace(Regex("^https"), "http")
                        else url)
                currentTasks.add(taskService.scheduleWithFixedDelay(
                        { handleUrlChecking(checkTask.id!!, parsedUrl) },
                        1,
                        checkTask.interval.toLong(),
                        TimeUnit.SECONDS
                ))
            } catch (ex: Exception) {
                logger.error("ERROR WHEN TRY TO PARSE URL: $url", ex)
            }
        }
    }

    private fun handleUrlChecking(taskId: String, url: URL) {
        checkUrlByStatus(url).let {
            taskHistoryRepository.save(CheckTaskHistory(
                    taskId = taskId,
                    url = url.toString(),
                    checkDate = OffsetDateTime.now(),
                    result = it
            ))
        }
    }

    private fun checkUrlByStatus(
            url: URL): Boolean {
        logger.debug("CHECKING URL: $url")
        try {
            val connection: HttpURLConnection =
                    url.openConnection() as HttpURLConnection
            var ok = false
            try {
                connection.requestMethod = "GET"
                connection.connect()
                connection.connectTimeout = 100
                connection.readTimeout = 100
                val status = connection.responseCode
                logger.debug("RESPONSE STATUS: $status URL: $url")
                ok = status in 200..399
            } catch (ex: UnknownHostException) {
                logger.debug("UNKNOWN HOST: $url")
            } finally {
                connection.disconnect()
                logger.debug("CONNECTION TO URL: $url DISCONNECTED")
            }
            return ok
        } catch (ex: Throwable) {
            logger.warn("Oops! Something went wrong =((", ex)
            return false
        }
    }

}