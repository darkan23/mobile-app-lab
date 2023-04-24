package labone.news.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import labone.AppScope

interface NewsRepository {
    fun flowNews(): Flow<List<News>>

    fun saveNews(news: News)
}

internal class NewsRepositoryImpl(
    private val appScope: AppScope,
    private val newsDao: NewsDao,
) : NewsRepository {

    override fun flowNews(): Flow<List<News>> = newsDao.flowNews()

    override fun saveNews(news: News) {
        appScope.launch(Dispatchers.IO) {
            newsDao.saveLocal(news)
        }
    }
}
