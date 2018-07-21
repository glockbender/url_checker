package com.prvz.url_checker.controller

import com.prvz.url_checker.*
import com.prvz.url_checker.service.TaskService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.time.OffsetDateTime
import javax.validation.Valid

@RestController
@RequestMapping("/api")
open class ApiController(
        private val taskService: TaskService
) {

    @PostMapping
    fun addTask(@Valid @RequestBody taskDto: CreateTaskDto,
                uriComponentsBuilder: UriComponentsBuilder): ResponseEntity<Any> {
        val task = taskService.newTask(CheckTask(
                createDate = OffsetDateTime.now(),
                interval = taskDto.interval,
                urls = taskDto.urls,
                checkType = CheckType.valueOf(taskDto.checkType.name)
        ))
        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/api/{id}")
                        .buildAndExpand(task.id)
                        .toUri())
                .build()

    }

    @PutMapping
    fun stopTask(@Valid @RequestBody taskDto: StopTaskDto?) {
        if (taskDto == null) {
            taskService.stopLast()
        } else {
            taskService.stopTask(taskDto.id)
        }
    }

    @GetMapping("/{id}")
    fun getTask(@PathVariable("id") id: String): ResponseEntity<CheckTaskDto> =
            taskService.getTaskInfo(id)
                    ?.let {
                        ok().body(CheckTaskDto(
                                id = it.id!!,
                                createDate = it.createDate,
                                closeDate = it.closeDate,
                                interval = it.interval,
                                urls = it.urls))
                    }
                    ?: notFound().build()

    @GetMapping("/{id}/history")
    fun getTaskHistory(@PathVariable("id") id: String, pageable: Pageable): Page<CheckTaskHistoryDto> =
            taskService.getTaskHistory(id, pageable)
                    .map { entity ->
                        CheckTaskHistoryDto(
                                id = entity.id!!,
                                taskId = entity.taskId,
                                url = entity.url,
                                checkDate = entity.checkDate,
                                result = entity.result
                        )
                    }


    @GetMapping
    fun test() {
        val task = CheckTask(
                createDate = OffsetDateTime.now(),
                interval = 10,
                urls = listOf("http://ya.ru", "https://mail.ru"),
                checkType = CheckType.BY_STATUS)
        taskService.newTask(task)
    }
}

