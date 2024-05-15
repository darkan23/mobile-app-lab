package ru.labone.addchat.ui

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.labone.Effects
import ru.labone.chats.data.Chat
import ru.labone.chats.data.ChatsRepository
import ru.labone.chats.data.Messages
import ru.labone.effects
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import java.time.Instant
import java.util.UUID

class AddChatViewModel @AssistedInject constructor(
    @Assisted initialState: AddChatState,
    private val chatsRepository: ChatsRepository,
) : MavericksViewModel<AddChatState>(initialState) {

    val effects: Effects<AddChatEffect> = effects()

    fun changeNameChat(text: String?) = setState { copy(nameChat = text) }

    fun saveNewChat() = withState { state ->
        val text = state.nameChat
        if (!text.isNullOrBlank()) {
            val uuiD = UUID.randomUUID().toString()
            val news = Chat(
                id = uuiD,
                name = text,
                Instant.now().toEpochMilli(),
                Messages()
            )
            chatsRepository.saveChat(news)
            effects.publish(NavigationBack)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AddChatViewModel, AddChatState> {
        override fun create(initialState: AddChatState): AddChatViewModel
    }

    companion object :
        DiMavericksViewModelFactory<AddChatViewModel, AddChatState>(AddChatViewModel::class.java)
}

data class AddChatState(
    val nameChat: String? = null,
) : MavericksState

object NavigationBack : AddChatEffect()

sealed class AddChatEffect
