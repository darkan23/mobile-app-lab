package ru.labone.addnews.ui

import android.net.Uri
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.labone.Effects
import ru.labone.FileData
import ru.labone.addnews.ui.UIDocumentType.AUDIO
import ru.labone.addnews.ui.UIDocumentType.DOCUMENT
import ru.labone.addnews.ui.UIDocumentType.PICTURE
import ru.labone.effects
import ru.labone.filemanager.ExceedingTheSize
import ru.labone.filemanager.FileManager
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import ru.labone.news.data.News
import ru.labone.news.data.NewsRepository
import java.time.Instant
import java.util.UUID

private const val MAX_SIZE_FILE = 10

class AddNewsViewModel @AssistedInject constructor(
    @Assisted initialState: AddNewsState,
    private val newsRepository: NewsRepository,
    private val fileManager: FileManager,
) : MavericksViewModel<AddNewsState>(initialState) {

    val effects: Effects<AddNewsEffect> = effects()

    fun changeText(text: String?) = setState { copy(text = text) }

    fun savePhoto(uriList: List<FileData>) = withState { state ->
        val oldFileData = state.fileData.toMutableList()
        val sizeFileData = oldFileData.size
        if (sizeFileData == MAX_SIZE_FILE) {

        } else {
            val newSize = MAX_SIZE_FILE - sizeFileData
            val asd = uriList.take(newSize)
            oldFileData.addAll(asd)
            setState { copy(fileData = oldFileData) }
            effects.publish(RenderPhoto(oldFileData.map { it.uri }, state.imagePosition))
        }
    }

    fun delete(pos: Int) = withState { state ->
        val newUriList = state.fileData.toMutableList()
        newUriList.removeAt(pos)
        val newPosition = pos - 1
        setState { copy(fileData = newUriList, imagePosition = newPosition) }
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

    private fun getDocumentType(mimeType: String): UIDocumentType? {
        val mimeTypeForAudio = "3gpp, mpeg, mp3, aac, wav, m4a"
        val mimeTypeForDocument = listOf("pdf, doc, docx")
        val mimeTypeForJpeg = listOf("jpeg", "jpg", "png")
        return when {
            mimeTypeForDocument.contains(mimeType) -> DOCUMENT
            mimeTypeForJpeg.contains(mimeType) -> PICTURE
            mimeTypeForAudio.contains(mimeType) -> AUDIO
            else -> null
        }
    }

    fun savePosition(position: Int) {
        setState {
            copy(
                imagePosition = position,
            )
        }
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
    val imagePosition: Int = 0,
) : MavericksState

enum class UIDocumentType {
    DOCUMENT,
    PICTURE,
    AUDIO
}

object NavigationBack : AddNewsEffect()

data class RenderPhoto(val photoUri: List<Uri>, val newPosition: Int) : AddNewsEffect()

sealed class AddNewsEffect
