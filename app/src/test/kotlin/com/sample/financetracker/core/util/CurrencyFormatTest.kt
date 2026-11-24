package com.sample.financetracker.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrencyFormatTest {
    @Test
    fun formatsWithIndianGrouping() {
        assertEquals("₹0.00", formatRupees(0))
        assertEquals("₹999.99", formatRupees(99_999))
        assertEquals("₹1,000.00", formatRupees(100_000))
        assertEquals("₹1,24,500.00", formatRupees(12_450_000))
        assertEquals("₹12,34,56,789.05", formatRupees(1_234_567_8905))
        assertEquals("-₹250.50", formatRupees(-25_050))
    }

    @Test
    fun formatsCompactInLakhsAndCrores() {
        assertEquals("₹99,999.00", formatRupeesCompact(9_999_900))
        assertEquals("₹12.45 L", formatRupeesCompact(124_500_000))
        assertEquals("₹1.20 Cr", formatRupeesCompact(1_200_000_000))
        assertEquals("-₹12.45 L", formatRupeesCompact(-124_500_000))
    }

    @Test
    fun parsesAmounts() {
        assertEquals(125_050L, parsePaise("1250.5"))
        assertEquals(100L, parsePaise("1"))
        assertEquals(1L, parsePaise("0.005"))
        assertNull(parsePaise(""))
        assertNull(parsePaise("abc"))
        assertNull(parsePaise("0"))
        assertNull(parsePaise("-5"))
    }

    @Test
    fun trimsWholeRupees() {
        assertEquals("₹34,550", formatRupeesTrimmed(3_455_000))
        assertEquals("₹34,550.50", formatRupeesTrimmed(3_455_050))
        assertEquals("₹1,20,000", formatRupeesTrimmed(12_000_000))
        assertEquals("85,450", formatIndianDigits(8_545_000))
        assertEquals("85,450", formatIndianDigits(-8_545_000))
    }

    @Test
    fun abbreviatesForTightSpaces() {
        assertEquals("₹640", formatRupeesAbbreviated(64_000))
        assertEquals("₹34.5k", formatRupeesAbbreviated(3_455_000))
        assertEquals("₹12k", formatRupeesAbbreviated(1_200_000))
        assertEquals("₹1.20 L", formatRupeesAbbreviated(12_000_000))
    }
}
