package ru.labone.news.ui

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import mu.KotlinLogging.logger
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import ru.labone.news.data.News
import ru.labone.news.data.NewsRepository

class NewsViewModel @AssistedInject constructor(
    @Assisted initialState: NewsState,
    val newsRepository: NewsRepository,
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

    fun test() {
        viewModelScope.launch {
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
    val text: String? = null,
) : MavericksState
