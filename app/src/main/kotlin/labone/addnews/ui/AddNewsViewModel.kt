package labone.addnews.ui

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MvRxState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DiMavericksViewModelFactory
import labone.news.data.News
import labone.news.data.NewsRepository
import java.time.Instant

class AddNewsViewModel @AssistedInject constructor(
    @Assisted initialState: AddNewsState,
    private val newsRepository: NewsRepository
) : MavericksViewModel<AddNewsState>(initialState) {

    fun saveNewNews() = withState { state ->
        viewModelScope.launch {
            val text = state.text
            if (text != null) {
                val news = News(
                    nameGroup = state.nameGroup,
                    text = text,
                    date = Instant.now().toEpochMilli()
                )
                newsRepository.saveNews(news)
            }
        }
    }

    fun changeText(text: String?) = setState { copy(text = text) }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AddNewsViewModel, AddNewsState> {
        override fun create(initialState: AddNewsState): AddNewsViewModel
    }

    companion object : DiMavericksViewModelFactory<AddNewsViewModel, AddNewsState>(AddNewsViewModel::class.java)
}

data class AddNewsState(
    val news: List<News> = emptyList(),
    val nameGroup: String = "",
    val text: String? = null
) : MvRxState
