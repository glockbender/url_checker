package com.prvz.url_checker.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Getter
public class CheckTaskHistoryDto {

    private final String id;
    private final String taskId;
    private final String url;
    private final OffsetDateTime checkDate;
    private final boolean result;

}
