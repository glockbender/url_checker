package com.prvz.url_checker.repository

import com.prvz.url_checker.CheckTaskHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface CheckTaskHistoryRepository : MongoRepository<CheckTaskHistory, String> {

    fun findAllByTaskId(id: String, pageable: Pageable): Page<CheckTaskHistory>
}