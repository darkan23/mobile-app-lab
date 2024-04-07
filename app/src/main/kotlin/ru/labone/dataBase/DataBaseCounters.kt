package ru.labone.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.labone.news.data.Document
import ru.labone.news.data.News
import ru.labone.news.data.NewsDao
import ru.labone.profile.CustomerProfile
import ru.labone.profile.ProfileDao

@Database(
    entities = [
        CustomerProfile::class,
        News::class,
        Document::class,
    ],
    version = 1,
    exportSchema = true
)

@TypeConverters(DbConverters::class)
abstract class DataBaseCounters : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    abstract fun newsDao(): NewsDao

}
