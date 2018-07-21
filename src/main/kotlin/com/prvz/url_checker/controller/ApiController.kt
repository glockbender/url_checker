package com.prvz.url_checker.controller

import com.prvz.url_checker.*
import com.prvz.url_checker.service.TaskService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.time.OffsetDateTime
import javax.validation.Valid

@RestController
@RequestMapping("/api")
open class ApiController(
        private val taskService: TaskService
) {

    @PostMapping
    fun addTask(@Valid @RequestBody taskDto: CreateTaskDto,
                uriComponentsBuilder: UriComponentsBuilder): ResponseEntity<Any> {
        val task = taskService.newTask(CheckTask(
                createDate = OffsetDateTime.now(),
                interval = taskDto.interval,
                urls = taskDto.urls,
                checkType = CheckType.valueOf(taskDto.checkType.name)
        ))
        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/api/{id}")
                        .buildAndExpand(task.id)
                        .toUri())
                .build()

    }

    @PutMapping
    fun stopTask(@Valid @RequestBody taskDto: StopTaskDto?) {
        if (taskDto == null) {
            taskService.stopLast()
        } else {
            taskService.stopTask(taskDto.id)
        }
    }

    @GetMapping("/{id}")
    fun getTask(@PathVariable("id") id: String): ResponseEntity<CheckTaskDto> =
            taskService.getTaskInfo(id)
                    ?.let { ok(toTaskDto(it)) }
                    ?: notFound().build()

    @GetMapping("/{id}/history")
    fun getTaskHistory(@PathVariable("id") id: String, pageable: Pageable): Page<CheckTaskHistoryDto> =
            taskService
                    .getTaskHistory(id, pageable)
                    .map(this::toTaskHistoryDto)


    @GetMapping
    fun getLast(): ResponseEntity<CheckTaskDto> =
            taskService.getLastInfo()
                    ?.let { ok(toTaskDto(it)) }
                    ?: notFound().build()

    @GetMapping("/history")
    fun getLastHistory(pageable: Pageable): Page<CheckTaskHistoryDto> =
            taskService.getLastHistory(pageable).map(this::toTaskHistoryDto)


    private fun toTaskDto(task: CheckTask) =
            CheckTaskDto(
                    id = task.id!!,
                    createDate = task.createDate,
                    closeDate = task.closeDate,
                    interval = task.interval,
                    urls = task.urls)

    private fun toTaskHistoryDto(history: CheckTaskHistory) =
            CheckTaskHistoryDto(
                    id = history.id!!,
                    taskId = history.taskId,
                    url = history.url,
                    checkDate = history.checkDate,
                    result = history.result
            )

}

