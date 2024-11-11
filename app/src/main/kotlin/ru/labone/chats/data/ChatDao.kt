package ru.labone.chats.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
abstract class ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun saveChat(chat: Chat)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun saveMessage(message: Message)

    @Query("UPDATE Message SET readDate = :readDate WHERE id = :id")
    abstract suspend fun markRead(id: String, readDate: Instant)

    @Query("UPDATE Message SET status = :status WHERE id = :id")
    abstract suspend fun updateStatus(id: String, status: Status)

    @Query("SELECT * FROM Chat ORDER BY date DESC")
    abstract fun flowChats(): Flow<List<Chat>>

    @Query("SELECT * FROM Message WHERE chatId = :chatId ORDER BY createDate DESC")
    abstract fun flowMessages(chatId: String?): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE chatId = :chatId ORDER BY createDate DESC")
    abstract fun findMessages(chatId: String?): List<Message>

    @Query("SELECT * FROM Chat WHERE id = :id ORDER BY date DESC")
    abstract fun flowChatById(id: String?): Flow<Chat?>
}
