package ru.labone.addnote.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.labone.naviagion.NavigationEffect

@Parcelize
data class AddNewsNavKey(
    val communityId: String? = null,
) : Parcelable, NavigationEffect
