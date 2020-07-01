package bonch.dev.service.driver

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.Ride
import bonch.dev.domain.entities.common.ride.RideStatus
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.interactor.driver.getpassenger.GetPassengerInteractor
import bonch.dev.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import com.google.gson.Gson

class DriverRideService : Service() {

    private val context = App.appComponent.getContext()
    private val notificatonManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private lateinit var intentChangeRide: Intent
    private lateinit var getPassengerInteractor: IGetPassengerInteractor

    private var mRunning = false

    private val CHANNEL_HEADS_UP = "CHANNEL_HEADS_UP"
    private val CHANNEL = "CHANNEL"

    companion object {
        const val CHANGE_RIDE_TAG = "CHANGE_RIDE_TAG"

        var isAppClose = false
    }


    override fun onCreate() {
        super.onCreate()

        mRunning = false

        getPassengerInteractor = GetPassengerInteractor()

        intentChangeRide = Intent(CHANGE_RIDE_TAG)

        createNotificationChannel()

        //connect to socket
        getPassengerInteractor.connectSocket { isSuccess ->
            if (isSuccess) {
                //change ride status
                getPassengerInteractor.subscribeOnChangeRide { data, _ ->
                    //show notification
                    if (data != null) changeRideNotification(data)

                    intentChangeRide.putExtra(CHANGE_RIDE_TAG, data)
                    sendBroadcast(intentChangeRide)
                }
            } else sendBroadcast(null)
        }
    }


    private fun changeRideNotification(data: String) {
        val ride = Gson().fromJson(data, Ride::class.java)?.ride

        if (ride != null) {
            val userIdLocal = getPassengerInteractor.getUserId()
            val userIdRemote = ride.driver?.id
            val status = ride.statusId

            //passenger cancel ride
            if (status == StatusRide.CANCEL.status && userIdLocal == userIdRemote) {
                //update status LOCAL
                RideStatus.status = StatusRide.CANCEL

                val notification = buildNotification(
                    getString(R.string.passangerCancelledRide),
                    "Нажмите для подробностей",
                    isHeadsUp = true
                )

                notificatonManager.notify(1, notification)
            }
        }
    }


    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        if (!mRunning) {
            mRunning = true

            val notification = buildNotification(
                "Ваша поездка", "Нажмите для подробностей",
                isHeadsUp = isAppClose
            )

            startForeground(1, notification)

            notificatonManager.notify(1, notification)
        }

        return START_NOT_STICKY
    }


    private fun buildNotification(
        title: String,
        content: String,
        isHeadsUp: Boolean
    ): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        val channelId = if (isHeadsUp) CHANNEL_HEADS_UP else CHANNEL

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSubText("Активная поездка")
            .setSmallIcon(R.drawable.ava)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)

        if (isHeadsUp) {
            notification
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
        }

        return notification.build()
    }


    override fun onBind(intent: Intent): IBinder? = null


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


    override fun onDestroy() {
        super.onDestroy()

        mRunning = false

        //remove all notification from bar
        notificatonManager.cancelAll()

        getPassengerInteractor.disconnectSocket()
    }
}