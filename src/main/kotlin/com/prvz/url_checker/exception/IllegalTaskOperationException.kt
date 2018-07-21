package com.prvz.url_checker.exception

open class IllegalTaskOperationException(val taskId: String, message: String? = null) :
        RuntimeException(message ?: "Некорректная операция с задачей: $taskId") {
    companion object {
        const val serialVersionUID = 1L
    }
}