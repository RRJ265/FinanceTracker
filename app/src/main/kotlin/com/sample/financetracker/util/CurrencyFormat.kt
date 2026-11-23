package com.sample.financetracker.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

private const val PAISE_PER_THOUSAND_RUPEES = 100_000L
private const val PAISE_PER_LAKH = 10_000_000L
private const val PAISE_PER_CRORE = 1_000_000_000L

/** Formats paise as ₹1,24,500.00 using Indian digit grouping. */
fun formatRupees(paise: Long): String {
    val absolute = abs(paise)
    val rupees = (absolute / 100).toString()
    val fraction = (absolute % 100).toString().padStart(2, '0')
    val sign = if (paise < 0) "-" else ""
    return "$sign₹${groupIndian(rupees)}.$fraction"
}

/** Formats paise as ₹12.45 L / ₹1.20 Cr for summaries; smaller amounts use the full format. */
fun formatRupeesCompact(paise: Long): String {
    val absolute = abs(paise)
    val sign = if (paise < 0) "-" else ""
    return when {
        absolute >= PAISE_PER_CRORE -> "$sign₹${"%.2f".format(Locale.US, absolute / PAISE_PER_CRORE.toDouble())} Cr"
        absolute >= PAISE_PER_LAKH -> "$sign₹${"%.2f".format(Locale.US, absolute / PAISE_PER_LAKH.toDouble())} L"
        else -> formatRupees(paise)
    }
}

/** Like [formatRupees] but drops ".00" when the amount is a whole number of rupees: ₹34,550 or ₹34,550.50. */
fun formatRupeesTrimmed(paise: Long): String {
    val full = formatRupees(paise)
    return if (abs(paise) % 100 == 0L) full.removeSuffix(".00") else full
}

/** The digits of [formatRupeesTrimmed] without the currency symbol or sign, e.g. 1,20,000. */
fun formatIndianDigits(paise: Long): String =
    formatRupeesTrimmed(abs(paise)).removePrefix("₹")

/** Short form for tight spaces: ₹34.5k below a lakh, ₹12.45 L and ₹1.20 Cr above. */
fun formatRupeesAbbreviated(paise: Long): String {
    val absolute = abs(paise)
    return when {
        absolute >= PAISE_PER_LAKH -> formatRupeesCompact(paise)
        absolute >= PAISE_PER_THOUSAND_RUPEES -> {
            val thousands = BigDecimal(absolute).movePointLeft(5).setScale(1, RoundingMode.DOWN)
                .stripTrailingZeros().toPlainString()
            "${if (paise < 0) "-" else ""}₹${thousands}k"
        }
        else -> formatRupeesTrimmed(paise)
    }
}

/** Parses user input such as "1250.5" into paise, or null if it is not a valid positive amount. */
fun parsePaise(input: String): Long? {
    val value = input.trim().toBigDecimalOrNull() ?: return null
    if (value.signum() <= 0) return null
    return value.movePointRight(2).setScale(0, RoundingMode.HALF_UP).toLong()
}

private fun groupIndian(digits: String): String {
    if (digits.length <= 3) return digits
    val lastThree = digits.takeLast(3)
    val rest = digits.dropLast(3).reversed().chunked(2).joinToString(",").reversed()
    return "$rest,$lastThree"
}
