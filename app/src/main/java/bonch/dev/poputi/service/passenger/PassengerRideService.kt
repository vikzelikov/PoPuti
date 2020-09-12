package bonch.dev.poputi.service.passenger

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.chat.MessageObject
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Ride
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.interactor.passenger.getdriver.GetDriverInteractor
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.presentation.modules.common.chat.view.ChatView
import com.google.gson.Gson


/**
 * Service for create and riding one-trip ride
 * Support and save data of ride in case close app
 * Notifications about every step of ride
 * */


class PassengerRideService : Service() {

    private val context = App.appComponent.getContext()
    private val notificatonManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    private lateinit var intentChangeRide: Intent
    private lateinit var intentOfferPrice: Intent
    private lateinit var intentDeleteOffer: Intent
    private lateinit var intentChat: Intent
    private lateinit var intentDriverLocation: Intent
    private lateinit var getDriverInteractor: IGetDriverInteractor

    private val CHANNEL_HEADS_UP = "CHANNEL_HEADS_UP"
    private val CHANNEL = "CHANNEL"

    companion object {
        const val CHANGE_RIDE_TAG = "CHANGE_RIDE_TAG"
        const val OFFER_PRICE_TAG = "OFFER_PRICE_TAG"
        const val DELETE_OFFER_TAG = "DELETE_OFFER_TAG"
        const val CHAT_TAG = "CHAT_TAG"
        const val DRIVER_GEO_TAG = "DRIVER_GEO_TAG"

        var isRunning = false
        var isAppClose = false
        var isChatClose = true
    }


    override fun onCreate() {
        super.onCreate()

        isRunning = false

        getDriverInteractor = GetDriverInteractor()

        intentChangeRide = Intent(CHANGE_RIDE_TAG)
        intentOfferPrice = Intent(OFFER_PRICE_TAG)
        intentDeleteOffer = Intent(DELETE_OFFER_TAG)
        intentChat = Intent(CHAT_TAG)
        intentDriverLocation = Intent(DRIVER_GEO_TAG)

        createNotificationChannel()

        createConnection()
    }


    private fun createConnection() {

        connectMainSocket()

        connectSocketGetGeoDriver()

    }


    private fun connectMainSocket() {
        getDriverInteractor.connectSocket { isSuccess ->
            if (isSuccess) {
                //change ride status
                getDriverInteractor.subscribeOnChangeRide { data, _ ->
                    //show notification
                    if (data != null) changeRideNotification(data)

                    intentChangeRide.putExtra(CHANGE_RIDE_TAG, data)
                    sendBroadcast(intentChangeRide)
                }

                //offer price from driver
                getDriverInteractor.subscribeOnOfferPrice { data, _ ->
                    //show notification
                    if (data != null) offerPriceNotification()

                    intentOfferPrice.putExtra(OFFER_PRICE_TAG, data)
                    sendBroadcast(intentOfferPrice)
                }

                //delete offer from driver
                getDriverInteractor.subscribeOnDeleteOffer { data, _ ->
                    intentDeleteOffer.putExtra(DELETE_OFFER_TAG, data)
                    sendBroadcast(intentDeleteOffer)
                }
            } else {
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        Handler().postDelayed({
                            connectMainSocket()
                        }, 1000)
                    }
                }
                mainHandler.post(myRunnable)
            }
        }

        //connect to chat socket
        getDriverInteractor.connectChatSocket { isSuccess ->
            if (isSuccess) {
                getDriverInteractor.subscribeOnChat { data, _ ->
                    //show notification
                    if (data != null) chatNotification(data)

                    intentChat.putExtra(CHAT_TAG, data)
                    sendBroadcast(intentChat)
                }
            }
        }
    }


    private fun connectSocketGetGeoDriver() {
        //connect to socket for get location of driver
        getDriverInteractor.connectSocketGetGeoDriver { isSuccess ->
            getDriverInteractor.subscribeOnGetGeoDriver { data, _ ->
                intentChat.putExtra(DRIVER_GEO_TAG, data)
                sendBroadcast(intentDriverLocation)
            }
            if (isSuccess) {
                getDriverInteractor.subscribeOnGetGeoDriver { data, _ ->
                    intentChat.putExtra(DRIVER_GEO_TAG, data)
                    sendBroadcast(intentDriverLocation)
                }
            } else {
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        Handler().postDelayed({
                            connectSocketGetGeoDriver()
                        }, 3500)
                    }
                }

                mainHandler.post(myRunnable)

            }
        }
    }


    private fun changeRideNotification(data: String) {
        val ride = Gson().fromJson(data, Ride::class.java)?.ride

        if (ride != null && isRunning) {
            ride.statusId?.let { idStep ->
                val statusRide = getByValue(idStep)

                statusRide?.let { status ->
                    //update ride LOCAL
                    ActiveRide.activeRide = ride

                    val (title, subtitle) = getRideStatusText(status)

                    val notification = buildNotification(
                        title, subtitle,
                        null,
                        isSetOngoing = true,
                        isAutoCancel = false,
                        isHeadsUp = isAppClose,
                        isChat = false
                    )

                    notificatonManager.notify(1, notification)

                }
            }
        }
    }


    private fun offerPriceNotification() {
        val isNotificationsEnable = CacheProfile.profile?.isNotificationsEnable

        if (isAppClose && (isNotificationsEnable == null || isNotificationsEnable)) {
            val title = getString(R.string.checkOffersFromDriver).plus(" 🛒")
            val subtitle = getString(R.string.selectBestOffer)

            val notification = buildNotification(
                title, subtitle,
                null,
                isSetOngoing = false,
                isAutoCancel = true,
                isHeadsUp = isAppClose,
                isChat = false
            )

            notificatonManager.notify(2, notification)
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

            var status = getByValue(ActiveRide.activeRide?.statusId)
            if (status == null) status = StatusRide.SEARCH

            val (title, subtitle) = getRideStatusText(status)

            val notification = buildNotification(
                title, subtitle,
                null,
                isSetOngoing = true,
                isAutoCancel = false,
                isHeadsUp = isAppClose,
                isChat = false
            )

            startForeground(1, notification)

            notificatonManager.notify(1, notification)

        }

        return START_STICKY
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
        isRunning = false
        isAppClose = false
        isChatClose = true

        //remove all notification from bar
        notificatonManager.cancelAll()

        getDriverInteractor.disconnectSocket()

        return super.stopService(name)
    }


    override fun onDestroy() {
        super.onDestroy()

        isRunning = false
        isAppClose = false
        isChatClose = true

        //remove all notification from bar
        notificatonManager.cancelAll()

        getDriverInteractor.disconnectSocket()
    }


    private fun getByValue(status: Int?) = StatusRide.values().firstOrNull { it.status == status }


    private fun getRideStatusText(status: StatusRide): Pair<String, String> {
        return when (status) {
            StatusRide.SEARCH -> Pair(
                getString(R.string.searchingDriver).plus(" 🔍"),
                getString(R.string.clickForDetail)
            )

            StatusRide.WAIT_FOR_DRIVER -> Pair(
                getString(R.string.driverInWay).plus(" 🚕"),
                getString(R.string.hurriesToYou)
            )

            StatusRide.WAIT_FOR_PASSANGER -> Pair(
                getString(R.string.driverArrived),
                getString(R.string.waitFreeTime)
            )

            StatusRide.IN_WAY -> Pair(
                getString(R.string.inWay),
                getString(R.string.clickForDetail)
            )

            StatusRide.GET_PLACE -> Pair(
                getString(R.string.youGetPlace),
                getString(R.string.rateRide)
            )

            StatusRide.CANCEL -> Pair(
                getString(R.string.driverCancelledRide),
                getString(R.string.clickForDetail)
            )

            else -> Pair(
                getString(R.string.errorSystem),
                ""
            )
        }
    }
}