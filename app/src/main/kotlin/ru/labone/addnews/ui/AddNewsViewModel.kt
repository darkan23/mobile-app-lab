package ru.labone.addnews.ui

import android.net.Uri
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.labone.Effects
import ru.labone.FileData
import ru.labone.effects
import ru.labone.filemanager.ExceedingTheSize
import ru.labone.filemanager.FileManager
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import ru.labone.news.data.News
import ru.labone.news.data.NewsRepository
import java.time.Instant
import java.util.UUID

class AddNewsViewModel @AssistedInject constructor(
    @Assisted initialState: AddNewsState,
    private val newsRepository: NewsRepository,
    private val fileManager: FileManager,
) : MavericksViewModel<AddNewsState>(initialState) {

    val effects: Effects<AddNewsEffect> = effects()

    fun changeText(text: String?) = setState { copy(text = text) }

    fun savePhoto(uriList: List<FileData>) {
        setState { copy(fileData = uriList) }
        effects.publish(RenderPhoto(uriList.map { it.uri }, 0))
    }

    fun delete(pos: Int) = withState { state ->
        val newUriList = state.fileData.toMutableList()
        newUriList.removeAt(pos)
        setState { copy(fileData = newUriList) }
        effects.publish(RenderPhoto(newUriList.map { it.uri }, pos - 1))
    }

    fun saveNewNews() = withState { state ->
        try {
            val text = state.text
            val filePaths = mutableListOf<String>()
            state.fileData.forEach { fileData ->
                val file = fileManager.copyDocumentFileToAppDir(fileData.uri, fileData.name)
                file?.path?.let { filePaths.add(it) }
            }
            if (!text.isNullOrBlank() || state.fileData.isNotEmpty()) {
                val uuiD = UUID.randomUUID().toString()
                println("FUCK ${filePaths.size}")
                val news = News(
                    id = uuiD,
                    nameGroup = state.nameGroup,
                    text = text.orEmpty(),
                    date = Instant.now().toEpochMilli(),
                    filePaths = filePaths
                )
                newsRepository.saveNews(news)
                effects.publish(NavigationBack)
            }
        } catch (e: ExceedingTheSize) {
            updateSelectedFile(generalError = "Размер файла превышает 100 МБ")
        }
    }

    private fun updateSelectedFile(fileData: List<FileData> = emptyList(), generalError: String? = null): Unit =
        setState {
            copy(
                fileData = fileData,
            )
        }

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
    val fileData: List<FileData> = emptyList(),
) : MavericksState

object NavigationBack : AddNewsEffect()

data class RenderPhoto(val photoUri: List<Uri>, val newPosition: Int) : AddNewsEffect()

sealed class AddNewsEffect
