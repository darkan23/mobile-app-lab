package ru.labone.addchat.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.labone.naviagion.NavigationEffect

@Parcelize
data class AddChatNavKey(
    val communityId: String? = null,
) : Parcelable, NavigationEffect
