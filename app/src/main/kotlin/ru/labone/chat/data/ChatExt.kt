package ru.labone.chat.data

import ru.labone.chats.data.Message
import ru.labone.chats.data.Person

fun Message.takeAuthorAvatar(
    currentAuthorId: String,
    nextAuthorId: String? = null,
): Person? = takeIf {
    nextAuthorId == null || currentAuthorId != nextAuthorId
}?.author

fun Message.takeAuthorName(
    currentAuthorId: String,
    previousAuthorId: String? = null,
): Person? = takeIf {
    previousAuthorId == null || currentAuthorId != previousAuthorId
}?.author
