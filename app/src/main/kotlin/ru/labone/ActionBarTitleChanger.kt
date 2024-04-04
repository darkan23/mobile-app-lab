package ru.labone

import androidx.annotation.StringRes

interface ActionBarTitleChanger {
    fun changeTitle(@StringRes titleRes: Int)
}
