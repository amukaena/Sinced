package com.sinced.domain.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtil {

    private val zone: ZoneId get() = ZoneId.systemDefault()

    fun todayMillis(): Long = LocalDate.now(zone)
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()

    fun toLocalDate(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()

    fun localDateToStartOfDayMillis(date: LocalDate): Long =
        date.atStartOfDay(zone).toInstant().toEpochMilli()

    fun daysBetween(fromMillis: Long, toMillis: Long): Long {
        val from = toLocalDate(fromMillis)
        val to = toLocalDate(toMillis)
        return ChronoUnit.DAYS.between(from, to)
    }

    fun addDays(millis: Long, days: Int): Long {
        val date = toLocalDate(millis).plusDays(days.toLong())
        return localDateToStartOfDayMillis(date)
    }

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun formatDate(millis: Long): String = dateFormatter.format(toLocalDate(millis))
}
