package com.prvz.url_checker.exception;

import org.springframework.util.StringUtils;

public class NoTasksException extends RuntimeException {

    private static final Long serialVersionUID = 1L;

    public NoTasksException(String message) {
        super(StringUtils.hasText(message) ?
                "В очереди нет задач" : message);
    }
}
