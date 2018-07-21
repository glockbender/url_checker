package com.prvz.url_checker.converter

import org.springframework.core.convert.converter.Converter
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

class OffsetDateTimeToDateConverter : Converter<OffsetDateTime, Date> {
    override fun convert(source: OffsetDateTime): Date =
            Date.from(source.toInstant())
}

class DateToOffsetDateTimeConverter : Converter<Date, OffsetDateTime> {
    override fun convert(source: Date): OffsetDateTime? =
            source.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime()
}