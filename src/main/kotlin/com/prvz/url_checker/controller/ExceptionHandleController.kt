package com.prvz.url_checker.controller

import com.prvz.url_checker.exception.IllegalTaskOperationException
import com.prvz.url_checker.exception.NoTasksException
import com.prvz.url_checker.exception.TaskAlreadyClosedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
open class ExceptionHandleController {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            HttpMessageNotReadableException::class,
            ServletRequestBindingException::class,
            IllegalTaskOperationException::class,
            NoTasksException::class,
            TaskAlreadyClosedException::class
    )
    fun processRequestException(e: Exception) {
        logger.error("Ошибка обращения к сервису", e)
    }

}