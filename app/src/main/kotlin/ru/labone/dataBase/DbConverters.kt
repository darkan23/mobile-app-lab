package ru.labone.dataBase

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.labone.chats.data.Messages
import ru.labone.chats.data.Person
import ru.labone.news.data.Documents
import ru.labone.profile.Gender
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

object DbConverters {

    @TypeConverter
    fun fromString(value: String?): List<String> =
        value?.let { it.split(',').map { it.trim() } } ?: emptyList()

    @TypeConverter
    fun toString(strings: List<String>): String = strings.joinToString(separator = ",")

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
    fun fromLocalDate(value: LocalDate?): String? =
        value?.let { DateTimeFormatter.ISO_LOCAL_DATE.format(value) }

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
    fun toUUID(value: String?): UUID? = value?.let { UUID.fromString(it) }

    @TypeConverter
    fun fromDocumentsToJSON(clothinglist: Documents): String = Gson().toJson(clothinglist)

    @TypeConverter
    fun fromJSONToDocuments(json: String): Documents = Gson().fromJson(json, Documents::class.java)

    @TypeConverter
    fun fromMessagesToJSON(clothinglist: Messages): String = Gson().toJson(clothinglist)

    @TypeConverter
    fun fromJSONToMessages(json: String): Messages = Gson().fromJson(json, Messages::class.java)

    @TypeConverter
    fun fromPersonToJSON(clothinglist: Person): String = Gson().toJson(clothinglist)

    @TypeConverter
    fun fromJSONToPerson(json: String): Person = Gson().fromJson(json, Person::class.java)
}
