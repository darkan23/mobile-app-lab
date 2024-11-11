package ru.labone.chats.data

import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant

@Entity(primaryKeys = ["id"])
data class Chat(
    val id: String,
    val name: String,
    val date: Long,
    val messages: Messages,
)

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class Message(
    val id: String,
    val chatId: String,
    val text: String? = null,
    val author: Person,
    val readDate: Instant? = null,
    val createDate: Instant,
    val status: Status,
)

private object Converters {
    @JvmStatic
    @TypeConverter
    fun fromStatus(value: String?): Status? = value?.let { Status.valueOf(it) }

    @JvmStatic
    @TypeConverter
    fun toStatus(value: Status?): String? = value?.name
}


data class Messages(
    val messages: List<Message> = emptyList(),
)

data class Person(
    val id: String,
    val name: String,
    val surname: String?,
    val photoId: Long
) {
    val fullName: String
        get() = "$name${surname?.let { " $surname" } ?: ""}"
}

enum class MessageType {
    QUESTION,
    ANSWERER,
    TECHNICAL
}
