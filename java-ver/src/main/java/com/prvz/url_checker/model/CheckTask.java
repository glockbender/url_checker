package com.prvz.url_checker.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

@Document(collection = "tasks")
@Getter
@EqualsAndHashCode
public final class CheckTask {

    @Indexed(direction = IndexDirection.ASCENDING)
    @Field("create_date")
    private final OffsetDateTime createDate;
    @Field("close_date")
    @Indexed(direction = IndexDirection.ASCENDING)
    private final OffsetDateTime closeDate;
    @Field("interval")
    private final Integer interval;
    @Field("urls")
    private final List<String> urls;
    @Id
    private String id;

    public CheckTask(OffsetDateTime createDate, OffsetDateTime closeDate, Integer interval, List<String> urls) {
        this.createDate = createDate;
        this.closeDate = closeDate;
        this.interval = interval;
        this.urls = urls;
    }

    @PersistenceConstructor
    public CheckTask(OffsetDateTime createDate, OffsetDateTime closeDate, Integer interval, List<String> urls, String id) {
        this.createDate = createDate;
        this.closeDate = closeDate;
        this.interval = interval;
        this.urls = urls;
        this.id = id;
    }
}
