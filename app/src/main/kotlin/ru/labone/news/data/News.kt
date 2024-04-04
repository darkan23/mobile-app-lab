package ru.labone.news.data

import androidx.room.Entity
import androidx.room.TypeConverter

@Entity(primaryKeys = ["id"])
data class News(
    val id: String,
    val nameGroup: String,
    val text: String,
    val date: Long,
    val filePaths: List<String> = emptyList(),
)
