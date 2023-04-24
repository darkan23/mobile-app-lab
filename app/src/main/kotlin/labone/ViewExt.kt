package labone

import android.net.Uri
import android.os.SystemClock
import android.view.View
import java.net.URI

fun URI.toUri(): Uri = Uri.parse(toString())

fun Uri.toURI(): URI = URI(toString())

const val DEFAULT_CLICK_DEBOUNCE_PERIOD_MILLIS = 600L

var lastClickTime: Long = 0

fun View.onClickWithDebounce(action: View.OnClickListener?) =
    onClickWithDebounce(action, debounceTime = DEFAULT_CLICK_DEBOUNCE_PERIOD_MILLIS)

fun View.onClickWithDebounce(action: View.OnClickListener?, debounceTime: Long) {
    if (action == null) {
        this.setOnClickListener(null)
    } else {
        this.setOnClickListener { v ->
            if (SystemClock.elapsedRealtime() - lastClickTime > debounceTime) {
                action.onClick(v)
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }
    }
}

inline fun <T, R> T?.ifNotNull(defaultValue: (T) -> R): R? = if (this != null) defaultValue(this) else null

inline fun <T> T?.ifNull(defaultValue: () -> T): T = this ?: defaultValue()
