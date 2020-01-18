package labone

import android.content.Context
import android.text.format.DateFormat
import android.widget.TextView
import com.example.labone.BuildConfig
import java.util.Locale

inline val Context.uiLocale: Locale
    get() = if (DateFormat.is24HourFormat(this)) Locale.UK else Locale.US


fun TextView.renderURL(url: String) = url

fun TextView.renderURL(photoId: Long) =
    renderURL("${BuildConfig.API_BASE_URL}photo/$photoId?photoShape=ROUND")

val pop
    get() = "${BuildConfig.API_BASE_URL}performance"