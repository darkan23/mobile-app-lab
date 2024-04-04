package ru.labone

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.labone.counters.Performance
import ru.labone.counters.TestImpl

internal class TestImplTest {

    @Test
    fun sum() {
        val testDate = listOf(
            Performance(1, "name1", 1, 1, 180, false),
            Performance(2, "name2", 1, 2, 120, false),
            Performance(3, "name3", 1, 3, 200, false),
            Performance(4, "name4", 1, 4, 300, false),
            Performance(5, "name5", 1, 5, 150, false),
            Performance(6, "name6", 1, 6, 50, false)
        )

        val test = TestImpl()

        val result = test.sumPrice(testDate)

        assertEquals(result, 166)
    }
}
