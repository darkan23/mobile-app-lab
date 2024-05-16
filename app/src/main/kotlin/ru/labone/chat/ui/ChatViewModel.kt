package ru.labone.chat.ui

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import mu.KotlinLogging.logger
import ru.labone.Effects
import ru.labone.chat.data.ChatNavKey
import ru.labone.chats.data.Chat
import ru.labone.chats.data.ChatMessage
import ru.labone.chats.data.ChatsRepository
import ru.labone.chats.data.Message
import ru.labone.chats.data.Person
import ru.labone.effects
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class ChatViewModel @AssistedInject constructor(
    @Assisted initialState: ChatState,
    private val chatsRepository: ChatsRepository,
) : MavericksViewModel<ChatState>(initialState) {

    private val log = logger {}
    val effect: Effects<ChatEffect> = effects()

    init {
        onEach(ChatState::chatId) { chatId ->
            chatsRepository.flowChatById(chatId).execute { chatDB: Async<Chat?> ->
                if (chatDB is Success) {
                    copy(
                        chat = chatDB()
                    )
                } else {
                    this
                }
            }

            chatsRepository.flowMessagesByChatId(chatId)
                .execute { messagesFromDB: Async<Map<LocalDate, List<ChatMessage>>> ->
                    if (messagesFromDB is Success) {
                        val messages = messagesFromDB()
                        copy(messages = messages)
                    } else {
                        this
                    }
                }
        }
    }

    fun changeMessage(message: String) = setState {
        copy(
            message = message,
        )
    }

    fun scrollToPosition(oldPosition: Int) = withState { state ->
        if (state.isInitialScroll) {
            val lastMessageItemPosition = state.messages.values.sumOf { messages ->
                val readMessageCount = messages.filter { it.readDate != null }.size
                if (messages.size > readMessageCount) {
                    readMessageCount + 1
                } else {
                    readMessageCount
                }
            } + state.messages.size
            effect.publish(ScrollToNextPosition(lastMessageItemPosition, true))
            setState { copy(isInitialScroll = false) }
        } else {
            val lastMessageItemPosition2 = state.messages.values.sumOf { messages -> messages.size }
            println("FUCK_1 $oldPosition $lastMessageItemPosition2")
            effect.publish(ScrollToNextPosition(oldPosition))
        }
    }

    fun saveMessage() = withState { state ->
        val uuid = UUID.randomUUID().toString()
        val chatId = state.chatId
        if (chatId != null) {
            val message = Message(
                uuid,
                state.chatId,
                state.message,
                Person("1", "My name", "Surname", 0),
                createDate = Instant.now(),
                readDate = Instant.now(),
            )
            chatsRepository.saveMessage(message)
        }
        setState {
            copy(message = "")
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ChatViewModel, ChatState> {
        override fun create(initialState: ChatState): ChatViewModel
    }

    companion object :
        DiMavericksViewModelFactory<ChatViewModel, ChatState>(ChatViewModel::class.java)
}

data class ChatState(
    val chatId: String? = null,
    val chat: Chat? = null,
    val messages: Map<LocalDate, List<ChatMessage>> = emptyMap(),
    val message: String = "",
    val isInitialScroll: Boolean = true,
) : MavericksState {
    constructor(args: ChatNavKey) : this(
        chatId = args.chatId,
    )
}

sealed class ChatEffect

data class ScrollToNextPosition(val index: Int, val isInitialScroll: Boolean = false) : ChatEffect()
