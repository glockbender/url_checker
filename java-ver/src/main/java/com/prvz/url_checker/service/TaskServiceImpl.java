package com.prvz.url_checker.service;

import com.prvz.url_checker.exception.NoTaskByIdException;
import com.prvz.url_checker.exception.NoTasksException;
import com.prvz.url_checker.exception.TaskAlreadyClosedException;
import com.prvz.url_checker.model.CheckTask;
import com.prvz.url_checker.model.CheckTaskHistory;
import com.prvz.url_checker.repository.CheckTaskHistoryRepository;
import com.prvz.url_checker.repository.CheckTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final CheckTaskRepository taskRepository;
    private final CheckTaskHistoryRepository taskHistoryRepository;

    private final ScheduledExecutorService taskService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final List<ScheduledFuture<?>> currentTasks = new ArrayList<>();
    private final Lock mainLock = new ReentrantLock();

    @PostConstruct
    private void init() {
        taskRepository.findTopByOrderByCreateDateDesc().ifPresent(task -> {
            if (task.getCloseDate() == null)
                runCheckTask(task);
        });
    }

    /**
     * Получение новой таски на исполнение.
     * Сохраняет новую таску в базу,
     * приостанавливает старую таску,
     * запускает новую таску
     */
    @Override
    public CheckTask newTask(CheckTask checkTask) {
        mainLock.lock();
        taskRepository.findTopByOrderByCreateDateDesc().ifPresent(this::closeAndCancelTask);
        CheckTask task = taskRepository.save(checkTask);
        runCheckTask(task);
        mainLock.unlock();
        return task;
    }

    @Override
    public void stopLast() {
        mainLock.lock();
        try {
            CheckTask task = taskRepository
                    .findTopByOrderByCreateDateDesc()
                    .orElseThrow(() -> new NoTasksException(null));
            if (task.getCloseDate() != null) {
                throw new TaskAlreadyClosedException(task.getId());
            }
            closeAndCancelTask(task);
        } finally {
            mainLock.unlock();
        }
    }

    private void closeAndCancelTask(CheckTask task) {
        taskRepository.save(new CheckTask(
                task.getCreateDate(),
                OffsetDateTime.now(),
                task.getInterval(),
                task.getUrls(),
                task.getId()
        ));
        cancelCurrentTasks();
    }

    private void cancelCurrentTasks() {
        if (currentTasks.isEmpty())
            return;
        currentTasks.forEach(f -> f.cancel(false));
        boolean isDone;
        do {
            isDone = currentTasks.stream().filter(Future::isDone).count() == currentTasks.size();
        } while (!isDone);
        currentTasks.clear();
    }

    @Override
    public CheckTask getTaskInfo(String id) {
        return taskRepository
                .findById(id)
                .orElseThrow(() -> new NoTaskByIdException(id));
    }

    @Override
    public CheckTask getLastInfo() {
        return taskRepository
                .findTopByOrderByCreateDateDesc()
                .orElseThrow(() -> new NoTasksException(null));
    }

    @Override
    public Page<CheckTask> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public Page<CheckTaskHistory> getTaskHistory(String taskId, Pageable pageable) {
        return taskHistoryRepository.findAllByTaskId(taskId, pageable);
    }

    @Override
    public Page<CheckTaskHistory> getLastHistory(Pageable pageable) {
        CheckTask task = taskRepository.findTopByOrderByCreateDateDesc().orElse(null);
        if (task != null) {
            return taskHistoryRepository.findAllByTaskId(task.getId(), pageable);
        } else return new PageImpl<>(emptyList());
    }

    private void runCheckTask(CheckTask checkTask) {
        checkTask.getUrls().forEach(urlStr -> {
            URL url;
            try {
                url = parseUrl(urlStr);
            } catch (Exception ex) {
                LOGGER.error("ERROR WHEN TRY TO PARSE URL", ex);
                return;
            }
            currentTasks.add(taskService.scheduleWithFixedDelay(
                    () -> handleUrlChecking(checkTask.getId(), url),
                    1,
                    checkTask.getInterval(),
                    TimeUnit.SECONDS
            ));
        });
    }

    private URL parseUrl(String url) throws MalformedURLException {
        if (url.startsWith("https")) {
            url = url.replaceFirst("^https", "http");
        }
        return new URL(url);
    }

    private void handleUrlChecking(String taskId, URL url) {
        boolean check = checkUrlByStatus(url);
        taskHistoryRepository.save(new CheckTaskHistory(
                taskId,
                url.toString(),
                OffsetDateTime.now(),
                check
        ));
    }

    private boolean checkUrlByStatus(URL url) {
        LOGGER.debug("CHECKING URL: {}", url);
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            boolean ok = false;
            try {
                conn.setRequestMethod("GET");
                conn.connect();
                conn.setConnectTimeout(500);
                conn.setReadTimeout(500);
                int status = conn.getResponseCode();
                LOGGER.debug("RESPONSE STATUS: {} URL: {}", status, url);
                if (status >= 200 && status < 400) {
                    ok = true;
                }
            } catch (UnknownHostException ex) {
                LOGGER.debug("UNKNOWN HOST: {}", url);
            } finally {
                conn.disconnect();
                LOGGER.debug("CONNECTION TO URL: {} DISCONNECTED", url);
            }
            return ok;
        } catch (Throwable ex) {
            LOGGER.warn("Oops! Something went wrong =((", ex);
            return false;
        }
    }
}
