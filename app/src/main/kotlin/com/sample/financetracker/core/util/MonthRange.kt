package com.sample.financetracker.core.util

import java.time.YearMonth
import java.time.ZoneId

fun YearMonth.startMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
    atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()

fun YearMonth.endMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
    plusMonths(1).startMillis(zone)
