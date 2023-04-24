package labone.addnews.ui

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MvRxState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import labone.Effects
import labone.effects
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DiMavericksViewModelFactory
import labone.news.data.News
import labone.news.data.NewsRepository
import java.time.Instant

class AddNewsViewModel @AssistedInject constructor(
    @Assisted initialState: AddNewsState,
    private val newsRepository: NewsRepository,
) : MavericksViewModel<AddNewsState>(initialState) {

    val effects: Effects<AddNewsEffect> = effects()

    fun saveNewNews() = withState { state ->
        val text = state.text
        if (text != null) {
            val news = News(
                nameGroup = state.nameGroup,
                text = text,
                date = Instant.now().toEpochMilli()
            )
            newsRepository.saveNews(news)
            effects.publish(NavigationBack)
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
    val text: String? = null,
) : MvRxState

object NavigationBack : AddNewsEffect()

sealed class AddNewsEffect