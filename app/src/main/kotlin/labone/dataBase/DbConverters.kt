package labone.dataBase

import androidx.room.TypeConverter
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parseList
import kotlinx.serialization.stringify
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.net.URI
import java.util.UUID

object DbConverters {

    @JvmStatic
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { str ->
            LocalDateTime.from(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(
                    str
                )
            )
        }
    }

    @JvmStatic
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.let { DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value) }
    }

    @JvmStatic
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { str -> LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(str)) }
    }

    @JvmStatic
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.let { DateTimeFormatter.ISO_LOCAL_DATE.format(value) }
    }

    @JvmStatic
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { str -> LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(str)) }
    }

    @JvmStatic
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.let { DateTimeFormatter.ISO_LOCAL_TIME.format(value) }
    }

    @JvmStatic
    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { epochMilli -> Instant.ofEpochMilli(epochMilli) }
    }

    @JvmStatic
    @TypeConverter
    fun fromInstant(value: Instant?): Long? {
        return value?.toEpochMilli()
    }

    @JvmStatic
    @TypeConverter
    fun fromUri(value: URI?): String? {
        return value?.toString()
    }

    @JvmStatic
    @TypeConverter
    fun toUri(value: String?): URI? {
        return value?.let { URI(it) }
    }

    @JvmStatic
    @TypeConverter
    fun fromUUID(value: UUID?): String? {
        return value?.toString()
    }

    @JvmStatic
    @TypeConverter
    fun toUUID(value: String?): UUID? {
        return value?.let { UUID.fromString(it) }
    }

    /**
     * Use Json.nonstrict for lists with 1 element
     */
    @ImplicitReflectionSerializer
    @UnstableDefault
    @JvmStatic
    @TypeConverter
    fun toStringList(list: List<String>): String {
        return Json.nonstrict.stringify(list)
    }

    /**
     * Use Json.nonstrict for lists with 1 element
     */
    @ImplicitReflectionSerializer
    @UnstableDefault
    @JvmStatic
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return Json.nonstrict.parseList(value)
    }
}
