package ru.labone.chats.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.labone.AppScope
import java.time.Instant

sealed class ChatMessage {
    abstract val id: String
    abstract val text: String?
    abstract val createDate: Instant
}

data class UserMessage(
    override val id: String,
    override val text: String? = null,
    override val createDate: Instant,
) : ChatMessage() {
    enum class Status(val isFailed: Boolean = false) {
        // processing
        SENDING,

        // fail
        NOT_SEND(true),
        NOT_PAID(true),

        // success
        AWAITING_PAYMENT,
        ASKED
    }
}

data class OtherUserMessage(
    override val id: String,
    override val text: String? = null,
    override val createDate: Instant,
    val author: Person,
) : ChatMessage()

interface ChatsRepository {
    fun flowChats(): Flow<List<Chat>>

    fun flowChatById(chatId: String?): Flow<List<Chat>>

    fun saveChat(chat: Chat)
}

class ChatsRepositoryImpl(
    private val appScope: AppScope,
    private val chatDao: ChatDao,
) : ChatsRepository {
    override fun flowChats(): Flow<List<Chat>> = chatDao.flowChats()

    override fun flowChatById(chatId: String?): Flow<List<Chat>> = chatDao.flowChatById(chatId)

    override fun saveChat(chat: Chat) {
        appScope.launch(Dispatchers.IO) {
            chatDao.saveChat(chat)
        }
    }
}
