package com.prvz.url_checker

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime

@Document(collection = "tasks")
data class CheckTask @PersistenceConstructor constructor(

        @field:Id
        val id: String? = null,
        @field:Indexed(direction = IndexDirection.ASCENDING)
        val createDate: OffsetDateTime,
        @field:Indexed(direction = IndexDirection.ASCENDING)
        val closeDate: OffsetDateTime? = null,
        val interval: Int,
        val urls: List<String>,
        val checkType: CheckType
)

@Document(collection = "tasksHistory")
data class CheckTaskHistory @PersistenceConstructor constructor(

        @field:Id
        val id: String? = null,
        @field:Indexed
        val taskId: String,
        val url: String,
        val checkDate: OffsetDateTime,
        val result: Boolean
)

enum class CheckType {
    PING_ONLY, BY_STATUS
}