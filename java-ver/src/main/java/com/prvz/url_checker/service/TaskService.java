package com.prvz.url_checker.service;

import com.prvz.url_checker.model.CheckTask;
import com.prvz.url_checker.model.CheckTaskHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    CheckTask newTask(CheckTask checkTask);

    void stopLast();

    CheckTask getTaskInfo(String id);

    CheckTask getLastInfo();

    Page<CheckTask> getAll(Pageable pageable);

    Page<CheckTaskHistory> getTaskHistory(String taskId, Pageable pageable);

    Page<CheckTaskHistory> getLastHistory(Pageable pageable);

}
