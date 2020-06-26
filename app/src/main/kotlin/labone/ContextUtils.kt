package labone

import android.content.Context
import android.text.Editable
import android.text.format.DateFormat
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.labone.BuildConfig
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
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

fun TextView.renderURL(photoId: Long) =
    renderURL("${BuildConfig.API_BASE_URL}photo/$photoId?photoShape=ROUND")

val pop
    get() = "${BuildConfig.API_BASE_URL}performance"