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
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.AddressPoint
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
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
        Log.w("NEW FB TOKEN", p0)
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

        p0.data.values.forEach {
            Log.e("DATA", "${it}")
        }

        val regularRide = RideInfo()
        regularRide.position = "ОТКУДААААА !3!"
        regularRide.destination = "DESTination"
        regularRide.fromLat = 59.915870
        regularRide.fromLng = 30.425529
        regularRide.toLat = 59.941434
        regularRide.toLng = 30.373617
        regularRide.price = 341

        if (title != null && subtitle != null) {
            val notification = buildNotification(
                title, subtitle,
                getString(R.string.newMessage),
                isSetOngoing = false,
                isAutoCancel = true,
                isHeadsUp = true,
                regularRide = regularRide
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
                "Default notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "General notification channel"
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager?.createNotificationChannel(channel)

            //heads up notification channel
            val channelHeadsUp = NotificationChannel(
                CHANNEL_HEADS_UP,
                "Important heads up notification",
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
        regularRide: RideInfo?
    ): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)

        if (regularRide != null) {
            val from = getFrom(regularRide)
            val to = getTo(regularRide)
            val price = regularRide.price

            if (from != null && to != null && price != null) {
                notificationIntent.putExtra("from", from)
                notificationIntent.putExtra("to", to)
                notificationIntent.putExtra("price", price)
            }
        }

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


    private fun getFrom(ride: RideInfo): Address? {
        val fromLat = ride.fromLat
        val fromLng = ride.fromLng

        val fromPoint = if (fromLat != null && fromLng != null) AddressPoint(fromLat, fromLng)
        else null

        return if (fromPoint != null) {
            val fromAdr = Address()
            fromAdr.point = fromPoint
            fromAdr.address = ride.position
            fromAdr

        } else null
    }


    private fun getTo(ride: RideInfo): Address? {
        val toLat = ride.toLat
        val toLng = ride.toLng

        val toPoint = if (toLat != null && toLng != null) AddressPoint(toLat, toLng)
        else null

        return if (toPoint != null) {
            val toAdr = Address()
            toAdr.point = toPoint
            toAdr.address = ride.destination
            toAdr
        } else null
    }
}
