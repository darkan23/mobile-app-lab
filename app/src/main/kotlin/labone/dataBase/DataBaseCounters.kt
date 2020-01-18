package labone.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import labone.counters.Performance
import labone.counters.PerformanceDao

@Database(entities = [Performance::class], version = 1, exportSchema = true)

@TypeConverters(DbConverters::class)
abstract class DataBaseCounters : RoomDatabase() {

    abstract fun countersDao(): PerformanceDao
}
