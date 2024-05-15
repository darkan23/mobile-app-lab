package ru.labone.chat.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.labone.naviagion.NavigationEffect

@Parcelize
data class ChatNavKey(
    val chatId: String? = null,
) : Parcelable, NavigationEffect
