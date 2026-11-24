package com.sample.financetracker.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun formatRecentDate(
    millis: Long,
    now: ZonedDateTime = ZonedDateTime.now(),
    zone: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault(),
): String {
    val moment = Instant.ofEpochMilli(millis).atZone(zone)
    val day = when (ChronoUnit.DAYS.between(moment.toLocalDate(), now.withZoneSameInstant(zone).toLocalDate())) {
        0L -> "Today"
        1L -> "Yesterday"
        else -> moment.format(DateTimeFormatter.ofPattern("MMM d", locale))
    }
    return "$day, ${moment.format(DateTimeFormatter.ofPattern("h:mm a", locale))}"
}
