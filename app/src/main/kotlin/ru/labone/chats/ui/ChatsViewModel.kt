package ru.labone.chats.ui

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import mu.KotlinLogging.logger
import ru.labone.chat.data.ChatNavKey
import ru.labone.chats.data.Chat
import ru.labone.chats.data.ChatMessage
import ru.labone.chats.data.ChatsRepository
import ru.labone.chats.data.Message
import ru.labone.chats.data.Messages
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import ru.labone.naviagion.Navigator

class ChatsViewModel @AssistedInject constructor(
    @Assisted initialState: ChatsState,
    chatsRepository: ChatsRepository,
    val navigator: Navigator,
) : MavericksViewModel<ChatsState>(initialState) {

    private val log = logger {}

    init {
        chatsRepository.flowChats()
            .execute { newsFromDb: Async<List<UiChat>> ->
                if (newsFromDb is Success) {
                    val chats = newsFromDb()
                    log.info { "Flow to all chats: $chats" }
                    copy(chats = chats)
                } else {
                    this
                }
            }
    }

    fun navToChat(id: String) {
        navigator.navigateTo(ChatNavKey(id))
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ChatsViewModel, ChatsState> {
        override fun create(initialState: ChatsState): ChatsViewModel
    }

    companion object :
        DiMavericksViewModelFactory<ChatsViewModel, ChatsState>(ChatsViewModel::class.java)
}

data class UiChat(
    val id: String,
    val name: String,
    val date: Long,
    val messages: List<ChatMessage>,
)

data class ChatsState(
    val chats: List<UiChat> = emptyList(),
) : MavericksState
