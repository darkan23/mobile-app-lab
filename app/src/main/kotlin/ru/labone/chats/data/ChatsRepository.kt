@file:OptIn(ExperimentalCoroutinesApi::class)

package ru.labone.chats.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.labone.AppScope
import ru.labone.chat.data.ChatGPTApi
import ru.labone.chat.data.ChatGPTBody
import ru.labone.chat.data.takeAuthorAvatar
import ru.labone.chat.data.takeAuthorName
import ru.labone.chats.ui.UiChat
import ru.labone.convertInstantToLocalDate
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

enum class Status(val isFailed: Boolean = false) {
    // processing
    SENDING,

    // fail
    NOT_SEND(true),

    // success
    SEND
}

sealed class ChatMessage {
    abstract val id: String
    abstract val text: String?
    abstract val createDate: Instant
    abstract val readDate: Instant?
}

data class UserMessage(
    override val id: String,
    override val text: String? = null,
    override val createDate: Instant,
    override val readDate: Instant? = null,
    val status: Status,
) : ChatMessage()

data class OtherUserMessage(
    override val id: String,
    override val text: String? = null,
    override val createDate: Instant,
    override val readDate: Instant? = null,
    val author: Person,
    val authorAvatar: String? = null,
    val authorName: String? = null,
) : ChatMessage()

interface ChatsRepository {
    fun flowChats(): Flow<List<UiChat>>

    fun flowChatById(chatId: String?): Flow<Chat?>

    fun flowMessagesByChatId(chatId: String?): Flow<Map<LocalDate, List<ChatMessage>>>

    fun saveChat(chat: Chat)

    fun saveMessage(message: Message)

    fun markRead(id: String)

    suspend fun askChatGPT(messageId: String, ask: String, chatId: String)
}

class ChatsRepositoryImpl(
    private val appScope: AppScope,
    private val chatDao: ChatDao,
    private val gptApi: ChatGPTApi,
) : ChatsRepository {
    override fun flowChats(): Flow<List<UiChat>> = chatDao.flowChats()
        .mapLatest { chats ->
            chats.map { chat ->
                val messages = chatDao.findMessages(chat.id)
                    .sortedBy { it.createDate.toEpochMilli() }
                    .convertToChatMessage()
                UiChat(
                    id = chat.id,
                    name = chat.name,
                    date = chat.date,
                    messages = messages,
                )
            }
        }

    override fun flowChatById(chatId: String?): Flow<Chat?> = chatDao.flowChatById(chatId)

    override fun flowMessagesByChatId(chatId: String?): Flow<Map<LocalDate, List<ChatMessage>>> =
        chatDao.flowMessages(chatId)
            .mapLatest { messages ->
                messages
                    .sortedBy { it.createDate.toEpochMilli() }
                    .groupBy { it.createDate.convertInstantToLocalDate() }
                    .mapValues { (_, messages) ->
                        messages.convertToChatMessage()
                    }
            }

    override fun saveChat(chat: Chat) {
        appScope.launch(Dispatchers.IO) {
            chatDao.saveChat(chat)
        }
    }

    override fun saveMessage(message: Message) {
        appScope.launch(Dispatchers.IO) {
            chatDao.saveMessage(message)
            askChatGPT(message.id, message.text.orEmpty(), message.chatId)
        }
    }

    override fun markRead(id: String) {
        appScope.launch(Dispatchers.IO) {
            chatDao.markRead(id, Instant.now())
        }
    }

    override suspend fun askChatGPT(messageId: String, ask: String, chatId: String) {
        try {
            val response = gptApi.completionResponse(
                ChatGPTBody(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        ru.labone.chat.data.Message(
                            role = "user",
                            content = ask,
                        )
                    )
                )
            )
            if (response.isSuccessful) {
                val body = response.body()
                val content = body?.choices?.firstOrNull()?.message?.content
                val uuid = UUID.randomUUID().toString()
                val message = Message(
                    id = uuid,
                    chatId = chatId,
                    text = content,
                    author = Person("4", "GPT", "Surname", 0),
                    createDate = Instant.now(),
                    status = Status.SEND,
                )
                updateStatus(messageId, Status.SEND)
                chatDao.saveMessage(message)
            } else {
                updateStatus(messageId, Status.NOT_SEND)
            }
        } catch (e: IOException) {
            updateStatus(messageId, Status.NOT_SEND)
        }
    }

    private fun updateStatus(messageId: String, status: Status) {
        appScope.launch(Dispatchers.IO) {
            chatDao.updateStatus(messageId, status)
        }
    }

    private fun List<Message>.convertToChatMessage(): List<ChatMessage> = mapIndexed { index, message ->
        val previousMessage = getOrNull(index - 1)
        val nextMessage = getOrNull(index + 1)
        when {
            message.author.id == "1" -> {
                UserMessage(
                    id = message.id,
                    text = message.text,
                    createDate = message.createDate,
                    readDate = message.readDate,
                    status = message.status,
                )
            }

            else -> {
                val currentAuthorId = message.author.id
                val previousAuthorId = previousMessage?.author?.id
                val nextAuthorId = nextMessage?.author?.id
                val authorAvatar =
                    message.takeAuthorAvatar(currentAuthorId, nextAuthorId)?.photoId
                val authorName =
                    message.takeAuthorName(currentAuthorId, previousAuthorId)?.name
                OtherUserMessage(
                    id = message.id,
                    text = message.text,
                    createDate = message.createDate,
                    readDate = message.readDate,
                    author = message.author,
                    authorAvatar = authorAvatar?.toString(),
                    authorName = authorName,
                )
            }
        }
    }
}
