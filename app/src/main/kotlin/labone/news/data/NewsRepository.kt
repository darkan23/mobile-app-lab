package labone.news.data

import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun saveNews(news: News)

    fun observeNews(): Flow<List<News>>
}

internal class NewsRepositoryImpl(private val newsDao: NewsDao) : NewsRepository {

    override suspend fun saveNews(news: News): Unit = newsDao.saveLocal(news)

    override fun observeNews(): Flow<List<News>> = newsDao.observe()
}
