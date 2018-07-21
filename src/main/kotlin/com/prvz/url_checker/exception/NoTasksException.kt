package com.prvz.url_checker.exception

open class NoTasksException(message: String? = null) :
        RuntimeException(message ?: "В очереди нет задач") {
    companion object {
        const val serialVersionUID = 1L
    }
}