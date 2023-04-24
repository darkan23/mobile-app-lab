package labone.news.ui

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DiMavericksViewModelFactory
import labone.news.data.News
import labone.news.data.NewsRepository
import mu.KotlinLogging.logger

class NewsViewModel @AssistedInject constructor(
    @Assisted initialState: NewsState,
    newsRepository: NewsRepository
) : MavericksViewModel<NewsState>(initialState) {

    private val log = logger {}

    init {
        newsRepository.flowNews()
            .execute { newsFromDb: Async<List<News>> ->
                if (newsFromDb is Success) {
                    val news = newsFromDb()
                    log.info { "allNews $news" }
                    copy(news = news)
                } else {
                    this
                }
            }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<NewsViewModel, NewsState> {
        override fun create(initialState: NewsState): NewsViewModel
    }

    companion object : DiMavericksViewModelFactory<NewsViewModel, NewsState>(NewsViewModel::class.java)
}

data class NewsState(
    val news: List<News> = emptyList(),
    val nameGroup: String = "",
    val text: String? = null
) : MvRxState
