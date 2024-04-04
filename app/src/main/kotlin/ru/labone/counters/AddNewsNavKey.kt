package ru.labone.counters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.labone.naviagion.NavigationEffect

@Parcelize
data class AddNewsNavKey(
    val id: Long,
) : Parcelable, NavigationEffect
