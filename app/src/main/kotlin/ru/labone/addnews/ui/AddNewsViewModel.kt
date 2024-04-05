package ru.labone.addnews.ui

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.labone.DocumentType
import ru.labone.DocumentType.PICTURE
import ru.labone.Effects
import ru.labone.FileData
import ru.labone.effects
import ru.labone.filemanager.ExceedingTheSize
import ru.labone.filemanager.FileManager
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import ru.labone.news.data.Document
import ru.labone.news.data.Documents
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
            effects.publish(ShowToast("Вы можете прикрипить не более 10 вложений."))
        } else {
            val newSize = MAX_SIZE_FILE - sizeFileData
            val lastFiles = uriList.take(newSize)
            lastFiles.forEach { newFile ->
                if (newFile.type == DocumentType.DOCUMENT) {
                    if (!oldFileData.any { it.name == newFile.name }) {
                        oldFileData.add(newFile)
                    } else {
                        effects.publish(ShowToast("Были удалены повторяющиеся вложения."))
                    }
                } else {
                    oldFileData.add(newFile)
                }
            }
            setState { copy(fileData = oldFileData) }
            effects.publish(RenderDocument(oldFileData, state.imagePosition))
        }
    }

    fun deleteImage(posImage: Int? = null, id: String) = withState { state ->
        val old = state.fileData
        val fileToDeleted = old.find { it.id == id }
        val newFiles = old.toMutableList()
        newFiles.remove(fileToDeleted)
        val oldPosition = posImage ?: state.imagePosition
        val newPosition = if (fileToDeleted?.type == PICTURE) oldPosition - 1 else oldPosition
        setState { copy(fileData = newFiles, imagePosition = newPosition) }
        effects.publish(RenderDocument(newFiles, newPosition))
    }

    fun saveNewNews() = withState { state ->
        try {
            val text = state.text
            val filePaths = mutableListOf<String>()
            val documents = state.fileData.map { fileData ->
                val file = fileManager.copyDocumentFileToAppDir(fileData.uri, fileData.name)
                val filePath = file?.path
                Document(
                    id = fileData.id,
                    name = fileData.name,
                    size = fileData.size,
                    path = filePath.orEmpty(),
                    type = fileData.type
                )
            }
            if (!text.isNullOrBlank() || state.fileData.isNotEmpty()) {
                val uuiD = UUID.randomUUID().toString()
                val news = News(
                    id = uuiD,
                    nameGroup = state.nameGroup,
                    text = text.orEmpty(),
                    date = Instant.now().toEpochMilli(),
                    document = Documents(documents),
                )
                newsRepository.saveNews(news)
                effects.publish(NavigationBack)
            }
        } catch (e: ExceedingTheSize) {
            updateSelectedFile(generalError = "Размер файла превышает 100 МБ")
        }
    }

    fun pickDocument() = withState { state ->
        val sizeFileData = state.fileData.size
        if (sizeFileData == MAX_SIZE_FILE) {
            effects.publish(ShowToast("Вы можете прикрипить не более 10 вложений."))
        } else {
            effects.publish(PickDocuments)
        }
    }

    private fun updateSelectedFile(fileData: List<FileData> = emptyList(), generalError: String? = null): Unit =
        setState {
            copy(
                fileData = fileData,
            )
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
    val nameGroup: String = "",
    val text: String? = null,
    val fileData: List<FileData> = emptyList(),
    val imagePosition: Int = 0,
) : MavericksState

object NavigationBack : AddNewsEffect()
object PickDocuments : AddNewsEffect()

data class ShowToast(val text: String) : AddNewsEffect()

data class RenderDocument(val photoUri: List<FileData>, val newPosition: Int) : AddNewsEffect()

sealed class AddNewsEffect
