package com.prvz.url_checker.repository

import com.prvz.url_checker.CheckTask
import org.springframework.data.mongodb.repository.MongoRepository

interface CheckTaskRepository :
        MongoRepository<CheckTask, String> {

    fun findTopByOrderByCreateDate(): CheckTask?
}