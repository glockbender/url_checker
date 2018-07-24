package com.prvz.url_checker.exception;

public class NoTaskByIdException extends IllegalTaskOperationException {

    private static final Long serialVersionUID = 1L;

    public NoTaskByIdException(String taskId) {
        super("Задачи с таким ID не существует: " + taskId, taskId);
    }
}
