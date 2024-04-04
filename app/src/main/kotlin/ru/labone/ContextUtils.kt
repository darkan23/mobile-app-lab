package ru.labone

import android.content.Context
import android.text.Editable
import android.text.format.DateFormat
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

inline val Context.uiLocale: Locale
    get() = if (DateFormat.is24HourFormat(this)) Locale.UK else Locale.US

fun Context.formatDate(date: LocalDate): String = DateTimeFormatter
    .ofPattern("dd MMM yyyy")
    .withLocale(uiLocale)
    .format(date)

fun TextView.renderURL(url: String) = url

inline fun TextView.doAfterTextChanged(crossinline action: (text: Editable?) -> Unit) =
    addTextChangedListener(afterTextChanged = action)

fun Fragment.formatDate(time: LocalDate): String = requireContext().formatDate(time)
