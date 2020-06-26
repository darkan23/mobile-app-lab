package labone.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import labone.counters.Performance
import labone.counters.PerformanceDao
import labone.news.data.News
import labone.news.data.NewsDao
import labone.profile.CustomerProfile
import labone.profile.ProfileDao

@Database(
    entities = [
        CustomerProfile::class,
        News::class,
        Performance::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(DbConverters::class)
abstract class DataBaseCounters : RoomDatabase() {

    abstract fun countersDao(): PerformanceDao

    abstract fun profileDao(): ProfileDao

    abstract fun newsDao(): NewsDao

}
