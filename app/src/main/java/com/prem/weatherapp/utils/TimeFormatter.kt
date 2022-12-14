package com.prem.weatherapp.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

object TimeFormatter {
    private val dayHourFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val clockHourOfDayFormatter = DateTimeFormatter.ofPattern("ha")
    private val hourOfDayFormatter = DateTimeFormatter.ofPattern("H")
    private val weekDayFormatter = DateTimeFormatter.ofPattern("EEEE")

    fun toDayHour(time: ZonedDateTime): String = dayHourFormatter.format(time)

    fun toDate(time: ZonedDateTime): String = DateTimeFormatter.RFC_1123_DATE_TIME.format(time)

    fun toHourOfDay(time: ZonedDateTime): String = clockHourOfDayFormatter.format(time)

    fun isMidnight(time: ZonedDateTime): Boolean = hourOfDayFormatter.format(time).equals("0")

    fun timeToAlpha(time: ZonedDateTime): Float {
        return abs(12 - hourOfDayFormatter.format(time).toFloat()) / 12
    }

    fun toWeekDay(time: ZonedDateTime): String = weekDayFormatter.format(time)
}