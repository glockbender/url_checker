package com.prvz.url_checker.web;

import com.prvz.url_checker.model.CheckTask;
import com.prvz.url_checker.model.CheckTaskHistory;
import com.prvz.url_checker.service.TaskService;
import com.prvz.url_checker.web.dto.CheckTaskDto;
import com.prvz.url_checker.web.dto.CheckTaskHistoryDto;
import com.prvz.url_checker.web.dto.CreateTaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.time.OffsetDateTime;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<?> addTask(@Valid @RequestBody CreateTaskDto taskDto,
                                     UriComponentsBuilder uriComponentsBuilder) {
        CheckTask task = taskService.newTask(new CheckTask(
                OffsetDateTime.now(),
                null,
                taskDto.getInterval(),
                taskDto.getUrls()
        ));
        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/api/{id}")
                        .buildAndExpand(task.getId())
                        .toUri())
                .build();
    }

    @PostMapping("/stop")
    public void stopLastTask() {
        taskService.stopLast();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckTaskDto> getTask(@PathVariable("id") String id) {
        return ok(toTaskDto(taskService.getTaskInfo(id)));
    }

    @GetMapping("/{id}/history")
    public Page<CheckTaskHistoryDto> getTaskHistory(@PathVariable("id") String id, Pageable pageable) {
        return taskService
                .getTaskHistory(id, pageable)
                .map(this::toTaskHistoryDto);
    }

    @GetMapping
    public ResponseEntity<Page<CheckTaskDto>> getAll(Pageable pageable) {
        return ok(taskService.getAll(pageable).map(this::toTaskDto));
    }

    @GetMapping("/last")
    public ResponseEntity<CheckTaskDto> getLast() {
        return ok(toTaskDto(taskService.getLastInfo()));
    }

    @GetMapping("/history")
    public Page<CheckTaskHistoryDto> getLastHistory(Pageable pageable) {
        return taskService.getLastHistory(pageable).map(this::toTaskHistoryDto);
    }

    private CheckTaskDto toTaskDto(CheckTask task) {
        return new CheckTaskDto(
                task.getId(),
                task.getCreateDate(),
                task.getCloseDate(),
                task.getInterval(),
                task.getUrls()
        );
    }

    private CheckTaskHistoryDto toTaskHistoryDto(CheckTaskHistory history) {
        return new CheckTaskHistoryDto(
                history.getId(),
                history.getTaskId(),
                history.getUrl(),
                history.getCheckDate(),
                history.isResult()
        );
    }

}
