package com.prvz.url_checker.exception;

public class TaskAlreadyClosedException extends IllegalTaskOperationException {

    private static final Long serialVersionUID = 1L;

    public TaskAlreadyClosedException(String taskId) {
        super("Задача c идентификтором: " + taskId + " уже завершена", taskId);
    }
}
