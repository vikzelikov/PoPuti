package bonch.dev.poputi.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.chat.view.ChatView
import bonch.dev.poputi.service.driver.DriverRideService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {


    private val context = App.appComponent.getContext()
    private val notificatonManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private lateinit var intentChangeRide: Intent

    private val CHANNEL_HEADS_UP = "CHANNEL_HEADS_UP"
    private val CHANNEL = "CHANNEL"


    override fun onNewToken(p0: String) {
        Log.w("TEST", "new token $p0")
        super.onNewToken(p0)
    }


    override fun onCreate() {
        super.onCreate()

        DriverRideService.isRunning = false

        intentChangeRide = Intent(DriverRideService.CHANGE_RIDE_TAG)

        createNotificationChannel()
    }


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val data = p0.notification
        val title = data?.title
        val subtitle = data?.body

        Log.e("TEST", "$title $subtitle")
        p0.data.values.forEach {
            Log.e("DATA", "${it}")
        }

        if (title != null && subtitle != null) {
            val notification = buildNotification(
                title, subtitle,
                getString(R.string.newMessage),
                isSetOngoing = false,
                isAutoCancel = true,
                isHeadsUp = true,
                isChat = false
            )

            notificatonManager.notify(1, notification)
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            //general notification channel
            val channel = NotificationChannel(
                CHANNEL,
                "GENERAL_CHANNEL",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "General notification channel"
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager?.createNotificationChannel(channel)

            //heads up notification channel
            val channelHeadsUp = NotificationChannel(
                CHANNEL_HEADS_UP,
                "HEADS_UP_CHANNEL",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Heads up notification channel"
            channelHeadsUp.setShowBadge(true)
            channelHeadsUp.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager?.createNotificationChannel(channelHeadsUp)
        }
    }


    private fun buildNotification(
        title: String,
        content: String,
        subText: String?,
        isSetOngoing: Boolean,
        isAutoCancel: Boolean,
        isHeadsUp: Boolean,
        isChat: Boolean
    ): Notification {
        val notificationIntent = if (isChat) Intent(this, ChatView::class.java)
        else Intent(this, MainActivity::class.java)

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        val channelId = if (isHeadsUp) CHANNEL_HEADS_UP else CHANNEL

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_system_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(isAutoCancel)
            .setOngoing(isSetOngoing)
            .setSubText(subText)
            .setColor(Color.parseColor("#1152FD"))

        if (isHeadsUp) {
            notification
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
        }

        return notification.build()
    }
}
