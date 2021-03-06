package bonch.dev.poputi.service.driver

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.chat.MessageObject
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.entities.common.ride.Ride
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.interactor.driver.getpassenger.GetPassengerInteractor
import bonch.dev.poputi.domain.interactor.driver.getpassenger.IGetPassengerInteractor
import bonch.dev.poputi.presentation.modules.common.chat.view.ChatView
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point

class DriverRideService : Service() {

    private val context = App.appComponent.getContext()
    private val notificatonManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private lateinit var intentChangeRide: Intent
    private lateinit var intentChat: Intent
    private var mLocationRequest: LocationRequest? = null
    private lateinit var getPassengerInteractor: IGetPassengerInteractor

    private val UPDATE_INTERVAL = 50 * 1000.toLong()
    private val FASTEST_INTERVAL = 5000L
    private lateinit var client: FusedLocationProviderClient
    private lateinit var callbackGeo: LocationCallback

    private val CHANNEL_HEADS_UP = "CHANNEL_HEADS_UP"
    private val CHANNEL = "CHANNEL"

    companion object {
        const val CHANGE_RIDE_TAG = "CHANGE_RIDE_TAG"
        const val CHAT_TAG = "CHAT_TAG"

        var isRunning = false
        var isAppClose = false
        var isChatClose = true
    }


    override fun onCreate() {
        super.onCreate()

        isRunning = false

        getPassengerInteractor = GetPassengerInteractor()

        intentChangeRide = Intent(CHANGE_RIDE_TAG)
        intentChat = Intent(CHAT_TAG)

        createNotificationChannel()

        startLocationUpdates()

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
            }
        }

        //connect to chat socket
        getPassengerInteractor.connectChatSocket { isSuccess ->
            if (isSuccess) {
                getPassengerInteractor.subscribeOnChat { data, _ ->
                    //show notification
                    if (data != null) chatNotification(data)

                    intentChat.putExtra(CHAT_TAG, data)
                    sendBroadcast(intentChat)
                }
            }
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
                val notification = buildNotification(
                    getString(R.string.passangerCancelledRide),
                    getString(R.string.clickForDetail),
                    null,
                    isSetOngoing = true,
                    isHeadsUp = true,
                    isAutoCancel = false,
                    isChat = false
                )

                notificatonManager.notify(1, notification)
            }
        }
    }


    private fun chatNotification(data: String) {
        if (isChatClose) {
            val message = Gson().fromJson(data, MessageObject::class.java)?.message
            val title = message?.author?.firstName
            val subtitle = message?.text

            val isNotificationsEnable = CacheProfile.profile?.isNotificationsEnable

            if (title != null && subtitle != null && (isNotificationsEnable == null || isNotificationsEnable)) {
                val notification = buildNotification(
                    title, subtitle,
                    getString(R.string.newMessage),
                    isSetOngoing = false,
                    isAutoCancel = true,
                    isHeadsUp = isChatClose,
                    isChat = true
                )

                notificatonManager.notify(3, notification)
            }
        }
    }


    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        if (!isRunning) {
            isRunning = true

            val notification = buildNotification(
                getString(R.string.activeRide),
                getString(R.string.clickForDetail),
                null,
                isSetOngoing = true,
                isHeadsUp = isAppClose,
                isAutoCancel = false,
                isChat = false
            )

            startForeground(1, notification)

            notificatonManager.notify(1, notification)
        }

        return START_NOT_STICKY
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


    override fun onBind(intent: Intent): IBinder? = null


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


    override fun stopService(name: Intent?): Boolean {
        stopLocationUpdates()

        return super.stopService(name)
    }


    override fun onDestroy() {
        super.onDestroy()

        isRunning = false

        //remove all notification from bar
        notificatonManager.cancelAll()

        getPassengerInteractor.disconnectSocket()

        stopLocationUpdates()
    }


    //we already checked geo permission
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = UPDATE_INTERVAL
        mLocationRequest?.fastestInterval = FASTEST_INTERVAL

        val builder = LocationSettingsRequest.Builder()

        mLocationRequest?.let {
            builder.addLocationRequest(it)
            val locationSettingsRequest = builder.build()

            val settingsClient = LocationServices.getSettingsClient(context)
            settingsClient.checkLocationSettings(locationSettingsRequest)

            client = LocationServices.getFusedLocationProviderClient(context)
            callbackGeo = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    val lat = location?.latitude
                    val lng = location?.longitude

                    if (lat != null && lng != null) {
                        val point = Point(lat, lng)
                        getPassengerInteractor.updateDriverGeo(point)
                    }
                }
            }

            client.requestLocationUpdates(mLocationRequest, callbackGeo, Looper.myLooper())
        }
    }


    private fun stopLocationUpdates() {
        client.removeLocationUpdates(callbackGeo)
    }
}