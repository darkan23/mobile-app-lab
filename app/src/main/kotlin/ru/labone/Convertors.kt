package ru.labone

import mu.KotlinLogging.logger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.TextStyle.FULL
import java.time.temporal.ChronoField.MONTH_OF_YEAR
import java.time.temporal.TemporalAccessor

object Convertors {

    private val log = logger {}

    private val YEAR_MONTH_FORMATTER = DateTimeFormatterBuilder()
        .appendText(java.time.temporal.ChronoField.DAY_OF_MONTH)
        .appendLiteral(' ')
        .appendText(MONTH_OF_YEAR, FULL)
        .toFormatter()

    private val FULL_YEAR_FORMATTER = DateTimeFormatterBuilder()
        .appendText(java.time.temporal.ChronoField.DAY_OF_MONTH)
        .appendLiteral(' ')
        .appendText(MONTH_OF_YEAR, FULL)
        .appendLiteral(' ')
        .appendText(java.time.temporal.ChronoField.YEAR)
        .toFormatter()

    fun temporalToUIDate(temporal: TemporalAccessor): String = formatTemporal(temporal, "dd HH:mm")

    fun temporalToUITime(temporal: TemporalAccessor): String = formatTemporal(temporal, "HH:mm")

    fun temporalToUIMinute(temporal: TemporalAccessor): String = formatTemporal(temporal, "mm")

    fun temporalToUIYearMonth(temporalAccessor: TemporalAccessor): String =
        formatTemporal(temporalAccessor, YEAR_MONTH_FORMATTER)

    fun temporalToUIFullYear(temporalAccessor: TemporalAccessor): String =
        formatTemporal(temporalAccessor, FULL_YEAR_FORMATTER)

    private fun formatTemporal(temporal: TemporalAccessor, pattern: String): String =
        formatTemporal(temporal, DateTimeFormatter.ofPattern(pattern))

    private fun formatTemporal(
        temporal: TemporalAccessor?,
        formatter: DateTimeFormatter,
    ): String = if (temporal == null) {
        log.warn { "Attempt to convert null temporal" }
        ""
    } else {
        when (temporal) {
            is Instant -> formatter.format(LocalDateTime.ofInstant(temporal, systemDefault()))
            is YearMonth -> formatter.format(temporal)
            is LocalTime -> formatter.format(temporal)
            else -> formatter.format((temporal as LocalDate).atTime(0, 0))
        }
    }
}
