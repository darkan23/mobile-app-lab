package ru.labone.chat.data

import ru.labone.chats.data.OtherUserMessage
import ru.labone.chats.data.Person

fun OtherUserMessage.takeAuthorAvatar(
    currentAuthorId: String,
    nextAuthorId: String? = null,
): Person? = takeIf {
    nextAuthorId == null || currentAuthorId != nextAuthorId
}?.author

fun OtherUserMessage.takeAuthorName(
    currentAuthorId: String,
    previousAuthorId: String? = null,
): Person? = takeIf {
    previousAuthorId == null || currentAuthorId != previousAuthorId
}?.author
