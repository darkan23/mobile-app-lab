package labone.counters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import labone.naviagion.NavigationEffect

@Parcelize
data class AddNewsNavKey(
    val id: Long,
) : Parcelable, NavigationEffect
