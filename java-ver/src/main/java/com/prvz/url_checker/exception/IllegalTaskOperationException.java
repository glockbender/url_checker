package com.prvz.url_checker.exception;


import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class IllegalTaskOperationException extends RuntimeException {

    private static final Long serialVersionUID = 1L;
    private final String taskId;

    public IllegalTaskOperationException(String message, String taskId) {
        super(StringUtils.hasText(message) ?
                ("Некорректная операция с задачей: " + taskId) : message);
        this.taskId = taskId;
    }
}
