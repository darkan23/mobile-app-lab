package labone.news.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class News(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val nameGroup: String,
    val text: String,
    val date: Long
)
