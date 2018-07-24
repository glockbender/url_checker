package com.prvz.url_checker.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prvz.url_checker.validation.UrlList;
import lombok.Getter;

import javax.validation.constraints.Positive;
import java.util.List;

@Getter
public class CreateTaskDto {

    @JsonProperty(value = "interval")
    @Positive
    private final Integer interval;

    @JsonProperty(value = "urls")
    @UrlList
    private final List<String> urls;

    @JsonCreator
    public CreateTaskDto(@Positive Integer interval, List<String> urls) {
        this.interval = interval;
        this.urls = urls;
    }
}
