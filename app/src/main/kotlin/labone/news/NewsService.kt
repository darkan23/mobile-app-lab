package labone.news

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.Completable.fromAction
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime

interface NewsService {
    fun saveNews(news: News): Disposable

    fun observ(): Flowable<out List<News>>
}

@Entity
data class News(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val nameGroup: String,
    val text: String,
    val date: Long
)

internal class NewsServiceImpl(private val newsDao: NewsDao) : NewsService {

    override fun saveNews(news: News): Disposable =
        fromAction { newsDao.saveLocal(news) }.subscribeOn(Schedulers.io()).subscribe()

    override fun observ(): Flowable<out List<News>> = newsDao.observe()

}
