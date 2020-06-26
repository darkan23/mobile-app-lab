package labone.news

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DaggerViewModelFactory
import mu.KotlinLogging.logger
import org.threeten.bp.Instant

class NewsViewModel @AssistedInject constructor(
    @Assisted initialState: NewsState,
    private val newsService: NewsService
) : BaseMvRxViewModel<NewsState>(initialState, BuildConfig.DEBUG) {

    val log = logger {}

    init {
        newsService.observ().toObservable()
            .execute { newss: Async<List<News>> ->
                if (newss is Success) {
                    val news = newss()
                    log.info { "allNews $news" }
                    copy(news = news)
                } else {
                    this
                }
            }
    }

    fun done() = withState { state ->
        val text = state.text
        if (text != null) {
            val news = News(
                nameGroup = state.nameGroup,
                text = text,
                date = Instant.now().toEpochMilli()
            )
            newsService.saveNews(news)
        }
    }

    fun changeText(text: String?) = setState { copy(text = text) }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<NewsViewModel, NewsState>

    companion object : DaggerViewModelFactory<NewsViewModel, NewsState>(
        NewsViewModel::class
    )
}

data class NewsState(
    val news: List<News> = emptyList(),
    val nameGroup: String = "",
    val text: String? = null
) : MvRxState
