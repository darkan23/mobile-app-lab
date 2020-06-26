package labone.counters

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PerformanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun savePerformance(performance: Performance): Long

    @Query("SELECT * FROM Performance")
    abstract fun observeActualCounters(): Flow<List<Performance>>

    @Query("SELECT COUNT(*) FROM Performance WHERE id IS NOT NULL")
    abstract fun observeUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(price) FROM Performance WHERE price IS NOT NULL")
    abstract fun observePriceCount(): Flow<Int>

    @Query("UPDATE Performance SET buy = :complete WHERE id = :id")
    abstract fun setComplete(id: Long, complete: Boolean)

    @Query("DELETE FROM Performance WHERE buy = 1")
    abstract fun clearCompletedTasks(): Int
}
