package ru.labone.news.data

import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.labone.DocumentType

@Entity(primaryKeys = ["id"])
data class News(
    val id: String,
    val nameGroup: String,
    val text: String,
    val date: Long,
    val document: Documents,
)

@Entity(primaryKeys = ["id"])
@TypeConverters(Converters::class)
data class Document(
    var id: String,
    var name: String,
    var size: Int,
    var path: String,
    var type: DocumentType,
)

data class Documents(
    val documents: List<Document>,
)

private object Converters {
    @JvmStatic
    @TypeConverter
    fun fromDocumentType(value: String?): DocumentType? = value?.let { DocumentType.valueOf(it) }

    @JvmStatic
    @TypeConverter
    fun toDocumentType(value: DocumentType?): String? = value?.name
}
