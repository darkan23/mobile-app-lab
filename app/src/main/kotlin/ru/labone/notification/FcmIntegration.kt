package ru.labone.notification

import dagger.hilt.android.AndroidEntryPoint
import mu.KotlinLogging.logger
import ru.rustore.sdk.pushclient.messaging.model.RemoteMessage
import ru.rustore.sdk.pushclient.messaging.service.RuStoreMessagingService
import javax.inject.Inject

private const val KEY_PUSH_ID: String = "id"
private const val KEY_PUSH_MESSAGE: String = "message"
private const val KEY_PUSH_CHANNEL: String = "channel_id"
private const val KEY_PUSH_TITLE: String = "title"
private const val CHANNEL: String = "Channel human readable title"

@AndroidEntryPoint
class FcmListeningService : RuStoreMessagingService() {

    @Inject
    internal lateinit var notificationService: NotificationService

    private val log = logger {}

    override fun onMessageReceived(message: RemoteMessage) {
        val notificationMap = message.data
        val title = message.notification?.title
        val body: String = message.notification?.body ?: ""
        val notificationId = message.notification?.body.hashCode() // id.toIntOrNull() ?: id.hashCode()
        val channelId: String = CHANNEL
        println("FUCK $notificationMap ${message.notification}")
        val notification = Notification(notificationId, body, channelId, title)

        notificationService.notify(notification)
    }

    override fun onNewToken(newToken: String) {
        if (newToken.isBlank()) {
            log.warn { "Invalid FCM token: $newToken" }
        } else {
            log.debug { "New FCM token received: $newToken" }
//            tokenManager.savePushToken(newToken)
        }
    }
}
