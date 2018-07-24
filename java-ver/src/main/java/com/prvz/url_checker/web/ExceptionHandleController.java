package com.prvz.url_checker.web;

import com.prvz.url_checker.exception.IllegalTaskOperationException;
import com.prvz.url_checker.exception.NoTaskByIdException;
import com.prvz.url_checker.exception.NoTasksException;
import com.prvz.url_checker.exception.TaskAlreadyClosedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ExceptionHandleController {

    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ServletRequestBindingException.class,
            IllegalTaskOperationException.class,
            NoTasksException.class,
            NoTaskByIdException.class,
            TaskAlreadyClosedException.class
    })
    public void processRequestException(Exception e) {
        LOGGER.error("Ошибка обращения к сервису", e);
    }

}
