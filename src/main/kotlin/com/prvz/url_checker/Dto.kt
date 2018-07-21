package com.prvz.url_checker

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.prvz.url_checker.validation.UrlsList
import java.time.OffsetDateTime

class CreateTaskDto @JsonCreator constructor(

        @field:JsonProperty(value = "interval")
        val interval: Int = 60,

        @field:UrlsList
        @field:JsonProperty(value = "urls")
        val urls: List<String>,

        @field:JsonProperty(value = "checkType")
        val checkType: CheckTypeDto = CheckTypeDto.BY_STATUS
)

class StopTaskDto @JsonCreator constructor(

        @field:JsonProperty(value = "id")
        val id: String
)

class CheckTaskDto(
        val id: String,
        val createDate: OffsetDateTime,
        val closeDate: OffsetDateTime?,
        val interval: Int,
        val urls: List<String>
)

class CheckTaskHistoryDto(
        val id: String,
        val taskId: String,
        val url: String,
        val checkDate: OffsetDateTime,
        val result: Boolean
)

enum class CheckTypeDto {
    PING_ONLY, BY_STATUS
}
