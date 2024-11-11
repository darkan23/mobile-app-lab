package ru.labone.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.labone.R
import com.jenzz.appstate.AppState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mu.KotlinLogging.logger
import ru.labone.AppScope
import ru.labone.MainActivity
import ru.labone.notification.NotificationService.Companion.PENDING_INTENT_REQUEST_CODE
import splitties.resources.appStr

data class Notification(
    val id: Int,
    val message: String,
    val channel: String,
    val title: String?,
)

interface NotificationService {
    fun notify(notification: Notification)

    companion object {
        // TODO implement navigation to entity
        const val PENDING_INTENT_REQUEST_CODE: Int = 42_001
    }
}

@SuppressLint("RxLeakedSubscription")
class NotificationServiceImpl(
    private val context: Context,
    appScope: AppScope,
    appState: Flow<AppState>,
) : NotificationService {

    private val notificationManager by lazy {
        requireNotNull(
            context.getSystemService<NotificationManager>()
        ) { "NotificationManager service is required." }
    }

    @Volatile
    private var currentAppState = AppState.BACKGROUND

    private val log = logger {}

    init {
        appScope.launch {
            appState.collect {
                currentAppState = it
                if (it == AppState.FOREGROUND) {
                    @Suppress("TooGenericExceptionCaught")
                    try {
                        notificationManager.cancelAll()
                    } catch (e: RuntimeException) {
                        log.warn(e) { "Fail to cancel active notifications" }
                    }
                }
            }
        }
    }

    override fun notify(notification: Notification) {
        if (currentAppState == AppState.FOREGROUND) {
            log.debug { "Push is received. Not showed, because current lifecycle is: $currentAppState" }
        } else {
            log.debug { "Push is received. Showing notification." }
            sendNotification(notification)
        }
    }

    private fun sendNotification(notification: Notification) {
        log.debug { "Sending received notification to Android: $notification" }
        // delete legacy channel
        notificationManager.createNotificationChannel(
            android.app.NotificationChannel(
                appStr(R.string.notifications_notification_push_channel_id),
                appStr(R.string.notifications_notification_push_channel_id),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        val appNotification = NotificationCompat.Builder(context, appStr(R.string.notifications_notification_push_channel_id)).apply {
            setAutoCancel(true)
            setSmallIcon(R.drawable.icon_edit)
            setTicker(notification.message)
            setContentText(notification.message)
            setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
            setColorized(true)
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setOnlyAlertOnce(true)
            val notifyIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            setContentIntent(
                PendingIntent.getActivity(
                    context,
                    PENDING_INTENT_REQUEST_CODE,
                    notifyIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            if (notification.title != null) {
                setContentTitle(notification.title)
            }
        }.build()

        notificationManager.notify(appStr(R.string.notifications_notification_push_channel_id), notification.id, appNotification)
    }
}
