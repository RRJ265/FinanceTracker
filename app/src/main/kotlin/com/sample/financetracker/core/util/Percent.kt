package com.sample.financetracker.core.util

fun percentChange(current: Long, previous: Long): Double? =
    if (previous <= 0) null else (current - previous) * 100.0 / previous
