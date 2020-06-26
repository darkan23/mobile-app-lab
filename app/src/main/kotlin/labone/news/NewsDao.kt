package labone.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
abstract class NewsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun saveLocal(news: News): Long

    @Query("SELECT * FROM News")
    abstract fun observe(): Flowable<List<News>>

}