package com.prvz.url_checker.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Document(collection = "tasksHistory")
@RequiredArgsConstructor
@Getter
public final class CheckTaskHistory {

    @Indexed
    private final String taskId;
    private final String url;
    private final OffsetDateTime checkDate;
    private final boolean result;
    @Id
    private String id;

    @PersistenceConstructor
    public CheckTaskHistory(String taskId, String url, OffsetDateTime checkDate, boolean result, String id) {
        this.taskId = taskId;
        this.url = url;
        this.checkDate = checkDate;
        this.result = result;
        this.id = id;
    }
}
