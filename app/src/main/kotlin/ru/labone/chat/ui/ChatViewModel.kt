package ru.labone.chat.ui

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import mu.KotlinLogging.logger
import ru.labone.chat.data.ChatNavKey
import ru.labone.chats.data.Chat
import ru.labone.chats.data.ChatMessage
import ru.labone.chats.data.ChatsRepository
import ru.labone.chats.data.Message
import ru.labone.chats.data.Messages
import ru.labone.chats.data.OtherUserMessage
import ru.labone.chats.data.Person
import ru.labone.chats.data.UserMessage
import ru.labone.convertInstantToLocalDate
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.DiMavericksViewModelFactory
import java.time.Instant
import java.time.LocalDate

class ChatViewModel @AssistedInject constructor(
    @Assisted initialState: ChatState,
    chatsRepository: ChatsRepository,
) : MavericksViewModel<ChatState>(initialState) {

    private val log = logger {}

    val objects = listOf(
        Message(
            "Object1",
            "Text1 Text1 Text1 Text1 Text1 Text1 ",
            Person("1", "Person1", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1550000215)
        ),
        Message(
            "Object2",
            "Text4Text4Text4 Text4Text4Text4",
            Person("1", "Person1", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702080400)
        ),
        Message(
            "Object5",
            "Text4 Text4 Text4 Text4 Text 4Text4 Text4 Text4 Text4 Text4",
            Person("1", "Person1", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1550000115)
        ),
        Message(
            "Object3",
            "Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4",
            Person("2", "Person2", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702080513)
        ),
        Message(
            "Object4",
            "Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4",
            Person("2", "Person2", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702080615)
        ),
        Message(
            "Object4",
            "Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4",
            Person("2", "Person2", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702080715)
        ),
        Message(
            "Object4",
            "Text4Text4Text5",
            Person("2", "Person2", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702080815)
        ),
        Message(
            "Object4",
            "Text4Text4Text6",
            Person("3", "Person3", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702080915)
        ),
        Message(
            "Object4",
            "Text4Text4Text66",
            Person("2", "Person2", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702081015)
        ),
        Message(
            "Object2",
            "Text4Text4Text4 Text4Text4Text4",
            Person("1", "Person1", "", 0),
            Instant.now(),
            Instant.ofEpochSecond(1702080550)
        ),
    )

    init {
        onEach(ChatState::chatId) { chatId ->
            chatsRepository.flowChatById(chatId)
                .map { chat ->
                    val asd = chat.first()
                    val messages = objects
                        .groupBy {
                            val date = it.createDate.convertInstantToLocalDate()
                            date
                        }
                        .mapValues { (_, messages) ->
                            messages
                                .map { message ->
                                    when {
                                        message.author.id == "1" -> UserMessage(message.id, message.text, message.createDate)
                                        else -> OtherUserMessage(message.id, message.text, message.createDate, message.author)
                                    }
                                }
                                .sortedBy { it.createDate.toEpochMilli() }
                        }
                    asd to messages
                }
                .execute { messagesFromDB: Async<Pair<Chat, Map<LocalDate, List<ChatMessage>>>> ->
                    if (messagesFromDB is Success) {
                        val messages = messagesFromDB()
                        copy(messages = messages)
                    } else {
                        this
                    }
                }
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
    val chatName: String = "",
    val messages: Pair<Chat, Map<LocalDate, List<ChatMessage>>> = Chat(
        "",
        "",
        Instant.now().toEpochMilli(),
        Messages()
    ) to emptyMap(),
) : MavericksState {
    constructor(args: ChatNavKey) : this(
        chatId = args.chatId,
    )
}
