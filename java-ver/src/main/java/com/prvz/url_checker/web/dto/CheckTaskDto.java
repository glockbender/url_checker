package com.prvz.url_checker.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CheckTaskDto {

    private final String id;
    private final OffsetDateTime createDate;
    private final OffsetDateTime closeDate;
    private final Integer interval;
    private final List<String> urls;

}
