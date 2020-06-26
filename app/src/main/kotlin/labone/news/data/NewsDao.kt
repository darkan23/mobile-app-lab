package labone.news.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NewsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun saveLocal(news: News)

    @Query("SELECT * FROM News")
    abstract fun observe(): Flow<List<News>>

}