package com.prvz.url_checker.exception

open class TaskAlreadyClosedException(taskId: String, message: String? = null) :
        IllegalTaskOperationException(taskId, message ?: "Задача c идентификтором $taskId уже завершена")