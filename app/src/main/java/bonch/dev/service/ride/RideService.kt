package bonch.dev.service.ride

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.presentation.modules.common.chat.view.ChatView


/**
 * Service for create and riding one-trip ride (regular driving included)
 * Support and save data of ride in case close app
 * Notifications about every step of ride
 * */


class RideService : Service() {

    private val context = App.appComponent.getContext()
    private val CHANNEL_ID = "RIDE_CHANNEL"


    override fun onCreate() {
        createNotificationChannel()
        super.onCreate()
    }

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Активная поездка")
            .setContentText("Смена статуса")
            .setSmallIcon(R.drawable.ava)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        val notificatonManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificatonManager.notify(1, notification)

        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder? = null


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val desription = "Ride channel notification"
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                desription,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = desription
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(notificationChannel)
        }
    }
}