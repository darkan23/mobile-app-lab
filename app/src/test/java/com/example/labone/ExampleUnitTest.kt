package com.example.labone

import labone.counters.Performance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `checking parsing incoming json`() {
        val details1 = Performance(null, "Perfor", 0, 0, 180, false)
        val details2 = Performance(null, "Perfor", 0, 0, 200, false)
        assertEquals(details1, details2)
        assertNotEquals(details1, null)
        assertNotEquals(details1, details2.copy(performanceName = "FEMALE"))
    }
}
