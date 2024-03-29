package labone.dataBase

import androidx.room.TypeConverter
import labone.profile.Gender
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

object DbConverters {

    @JvmStatic
    @TypeConverter
    fun fromGender(value: String?): Gender? = value?.let { Gender.valueOf(it) }

    @JvmStatic
    @TypeConverter
    fun toGender(value: Gender?): String? = value?.name

    @JvmStatic
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { str ->
        LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(str))
    }

    @JvmStatic
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? =
        value?.let { DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value) }

    @JvmStatic
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? =
        value?.let { str -> LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(str)) }


    @JvmStatic
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.let { DateTimeFormatter.ISO_LOCAL_DATE.format(value) }

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
}
