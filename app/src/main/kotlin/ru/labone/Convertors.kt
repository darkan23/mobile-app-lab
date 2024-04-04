package ru.labone

import mu.KotlinLogging.logger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

object Convertors {

    private val log = logger {}

    fun temporalToUIDate(temporal: TemporalAccessor): String = formatTemporal(temporal, "dd HH:mm")

    fun temporalToUITime(temporal: TemporalAccessor): String = formatTemporal(temporal, "HH:mm")

    fun temporalToUIMinute(temporal: TemporalAccessor): String = formatTemporal(temporal, "mm")

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
            is Instant -> formatter.format(LocalDateTime.ofInstant(temporal, ZoneId.systemDefault()))
            is YearMonth -> formatter.format(temporal)
            is LocalTime -> formatter.format(temporal)
            else -> formatter.format((temporal as LocalDate).atTime(0, 0))
        }
    }
}
