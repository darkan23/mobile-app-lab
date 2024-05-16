package ru.labone.chats.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.labone.AppScope
import ru.labone.chat.data.takeAuthorAvatar
import ru.labone.chat.data.takeAuthorName
import ru.labone.convertInstantToLocalDate
import java.time.Instant
import java.time.LocalDate

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
    override val readDate: Instant? = null,
    val author: Person,
    val authorAvatar: String? = null,
    val authorName: String? = null,
) : ChatMessage()

val objects = listOf(
    Message(
        "Object1",
        "Object1",
        "Text1 Text1 Text1 Text1 Text1 Text1 ",
        Person("1", "Person1", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1550000215)
    ),
    Message(
        "Object2",
        "Object1",
        "Text4Text4Text4 Text4Text4Text4",
        Person("1", "Person1", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1702080400)
    ),
    Message(
        "Object3",
        "Object1",
        "Text4 Text4 Text4 Text4 Text 4Text4 Text4 Text4 Text4 Text4",
        Person("1", "Person1", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1550000115)
    ),
    Message(
        "Object4",
        "Object1",
        "Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4",
        Person("2", "Person2", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1704080513)
    ),
    Message(
        "Object5",
        "Object1",
        "Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4",
        Person("2", "Person2", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1705080615)
    ),
    Message(
        "Object6",
        "Object1",
        "Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4Text4Text4Text4 Text4Text4Text4 Text4Text4Text4Text4 Text4Text4Text 4Text4Text4Text4",
        Person("2", "Person2", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1712080715)
    ),
    Message(
        "Object7",
        "Object1",
        "Text4Text4Text5",
        Person("2", "Person2", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1702080815)
    ),
    Message(
        "Object8",
        "Object1",
        "Text4Text4Text6",
        Person("3", "Person3", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1702080915)
    ),
    Message(
        "Object9",
        "Object1",
        "Text4Text4Text66",
        Person("2", "Person2", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1702081015)
    ),
    Message(
        "Object10",
        "Object1",
        "Text4Text4Text4 Text4Text4Text4",
        Person("1", "Person1", "", 0),
        Instant.now(),
        Instant.ofEpochSecond(1702080550)
    ),
    Message(
        "Object11",
        "Object1",
        "Вот пример статьи на 1000 символов. Это достаточно маленький текст, оптимально подходящий для карточек товаров в интернет-магазинах или для небольших информационных публикаций. В таком тексте редко бывает более 2-3 абзацев и обычно один подзаголовок. Но можно и без него. На 1000 символов рекомендовано использовать 1-2 ключа и одну картину.\n" +
                "\n" +
                "Текст на 1000 символов – это сколько примерно слов? Статистика Word показывает, что «тысяча» включает в себя 150-200 слов средней величины. Но, если злоупотреблять предлогами, союзами и другими частями речи на 1-2 символа, то количество слов неизменно возрастает.\n" +
                "\n" +
                "В копирайтерской деятельности принято считать «тысячи» с пробелами или без. Учет пробелов увеличивает объем текста примерно на 100-200 символов – именно столько раз мы разделяем слова свободным пространством. Считать пробелы заказчики не любят, так как это «пустое место». Однако некоторые фирмы и биржи видят справедливым ставить стоимость за 1000 символов с пробелами, считая последние важным элементом качественного восприятия. Согласитесь, читать слитный текст без единого пропуска, никто не будет. Но большинству нужна цена за 1000 знаков без пробелов.\n" +
                "\n" +
                "Вот пример статьи на 1000 символов. Это достаточно маленький текст, оптимально подходящий для карточек товаров в интернет-магазинах или для небольших информационных публикаций. В таком тексте редко бывает более 2-3 абзацев и обычно один подзаголовок. Но можно и без него. На 1000 символов рекомендовано использовать 1-2 ключа и одну картину.\n" +
                "\n" +
                "Текст на 1000 символов – это сколько примерно слов? Статистика Word показывает, что «тысяча» включает в себя 150-200 слов средней величины. Но, если злоупотреблять предлогами, союзами и другими частями речи на 1-2 символа, то количество слов неизменно возрастает.\n" +
                "\n" +
                "В копирайтерской деятельности принято считать «тысячи» с пробелами или без. Учет пробелов увеличивает объем текста примерно на 100-200 символов – именно столько раз мы разделяем слова свободным пространством. Считать пробелы заказчики не любят, так как это «пустое место». Однако некоторые фирмы и биржи видят справедливым ставить стоимость за 1000 символов с пробелами, считая последние важным элементом качественного восприятия. Согласитесь, читать слитный текст без единого пропуска, никто не будет. Но большинству нужна цена за 1000 знаков без пробелов.",
        Person("2", "Person2", "", 0),
        Instant.now(),
        Instant.now()
    ),
)

interface ChatsRepository {
    fun flowChats(): Flow<List<Chat>>

    fun flowChatById(chatId: String?): Flow<Chat?>

    fun flowMessagesByChatId(chatId: String?): Flow<Map<LocalDate, List<ChatMessage>>>

    fun saveChat(chat: Chat)

    fun saveMessage(message: Message)
}

class ChatsRepositoryImpl(
    private val appScope: AppScope,
    private val chatDao: ChatDao,
) : ChatsRepository {
    override fun flowChats(): Flow<List<Chat>> = chatDao.flowChats()

    override fun flowChatById(chatId: String?): Flow<Chat?> = chatDao.flowChatById(chatId)

    override fun flowMessagesByChatId(chatId: String?): Flow<Map<LocalDate, List<ChatMessage>>> =
        chatDao.flowMessages(chatId)
            .mapLatest { messages ->
                (objects + messages)
                    .sortedBy { it.createDate.toEpochMilli() }
                    .groupBy {
                        val date = it.createDate.convertInstantToLocalDate()
                        date
                    }
                    .mapValues { (_, messages) ->
                        messages
                            .mapIndexed { index, message ->
                                val previousMessage = messages.getOrNull(index - 1)
                                val nextMessage = messages.getOrNull(index + 1)
                                when {
                                    message.author.id == "1" -> {
                                        UserMessage(
                                            message.id,
                                            message.text,
                                            message.createDate,
                                            message.readDate,
                                        )
                                    }

                                    else -> {
                                        val currentAuthorId = message.author.id
                                        val previousAuthorId = previousMessage?.author?.id
                                        val nextAuthorId = nextMessage?.author?.id
                                        val authorAvatar =
                                            message.takeAuthorAvatar(
                                                currentAuthorId,
                                                nextAuthorId
                                            )?.photoId
                                        val authorName =
                                            message.takeAuthorName(
                                                currentAuthorId,
                                                previousAuthorId
                                            )?.name
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
            }

    override fun saveChat(chat: Chat) {
        appScope.launch(Dispatchers.IO) {
            chatDao.saveChat(chat)
        }
    }

    override fun saveMessage(message: Message) {
        appScope.launch(Dispatchers.IO) {
            chatDao.saveMessage(message)
        }
    }
}
