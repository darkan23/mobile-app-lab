package ru.labone

import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore.MediaColumns
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.load
import java.io.File
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

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

fun Instant.convertInstantToLocalDate(): LocalDate = this.atZone(ZoneId.systemDefault()).toLocalDate()

fun Instant.convertInstantToLocalTime(): LocalTime = this.atZone(ZoneId.systemDefault()).toLocalTime()

fun Instant.convertInstantToLocalDateTime(): LocalDateTime = this.atZone(ZoneId.systemDefault()).toLocalDateTime()

fun Instant.convertTimeToMinutes(): String {
    val currentTime = Instant.now().toEpochMilli()
    val diff = currentTime - this.toEpochMilli()
    val minutes = diff / (60 * 1000) % 60
    return when (minutes % 10) {
        1L -> "$minutes минуту назад"
        in 2..4 -> "$minutes минуты назад"
        else -> "$minutes минут назад"
    }
}

private const val TODAY = "Сегодня"
private const val YESTERDAY = "Вчера"

fun Instant.convertTimeTo(): String {
    val targetLocalDate = this.convertInstantToLocalDate()
    val targetLocalTime = this.convertInstantToLocalTime()
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()
    return when {
        targetLocalDate.dayOfMonth == currentDate.dayOfMonth -> {
            when {
                currentTime.hour - targetLocalTime.hour < 1 -> convertTimeToMinutes()
                currentTime.hour - targetLocalTime.hour == 1 -> "Час назад"
                currentTime.hour - targetLocalTime.hour == 2 -> "Два часа назад"
                currentTime.hour - targetLocalTime.hour == 3 -> "Три часа назад"
                else -> "$TODAY в ${Convertors.temporalToUITime(this)}"
            }
        }

        currentDate.dayOfMonth - targetLocalDate.dayOfMonth == 1 -> "$YESTERDAY в ${Convertors.temporalToUITime(this)}"
        else -> Convertors.temporalToUIDate(this)
    }
}

private const val ROUNDED_CORNER = 25f

fun ImageView.loadWithRoundedCorners(data: Any? = null) {
    val correctData = when (data) {
        is String -> {
            if(data.contains("files/documents")) Uri.fromFile(File(data)) else Uri.parse(data)
            Uri.fromFile(File(data))
        }
        else -> data
    }
    val context = this.context
    load(correctData) {
        transformations(
            AppRoundedCornersTransformation(
                ROUNDED_CORNER,
                ROUNDED_CORNER,
                ROUNDED_CORNER,
                ROUNDED_CORNER,
                context
            )
        )
    }
}

fun Fragment.getFileData(uriList: List<Uri>): List<FileData> {
    val contentResolver = requireActivity().contentResolver
    val projection = arrayOf(MediaColumns.DISPLAY_NAME)
    val fileData = mutableListOf<FileData>()
    uriList.forEach { uri ->
        contentResolver.query(uri, projection, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME)
                val name = cursor.getString(nameIndex)
                val newFileData = FileData(uri, name)
                fileData.add(newFileData)
            }
        }
    }
    return fileData
}

data class FileData(
    val uri: Uri,
    val name: String,
)

fun String?.orEmpty(): String = if (this.isNullOrBlank()) "" else this
