package bonch.dev.service.ride.passenger

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.domain.interactor.passenger.getdriver.GetDriverInteractor
import bonch.dev.domain.interactor.passenger.getdriver.IGetDriverInteractor
import com.google.gson.Gson
import java.util.concurrent.atomic.AtomicInteger


/**
 * Service for create and riding one-trip ride
 * Support and save data of ride in case close app
 * Notifications about every step of ride
 * */


class RideService : Service() {

    private val context = App.appComponent.getContext()
    private val notificatonManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private lateinit var intentOfferPrice: Intent
    private lateinit var intentChangeRide: Intent
    private lateinit var getDriverInteractor: IGetDriverInteractor

    private val notifyId = AtomicInteger(2)
    private var mRunning = false

    private val CHANNEL_HEADS_UP = "CHANNEL_HEADS_UP"
    private val CHANNEL = "CHANNEL"

    companion object {
        const val OFFER_PRICE_TAG = "OFFER_PRICE_TAG"
        const val CHANGE_RIDE_TAG = "CHANGE_RIDE_TAG"

        var isAppClose = false
    }


    override fun onCreate() {
        super.onCreate()

        mRunning = false

        getDriverInteractor = GetDriverInteractor()

        intentOfferPrice = Intent(OFFER_PRICE_TAG)
        intentChangeRide = Intent(CHANGE_RIDE_TAG)

        createNotificationChannel()

        //connect to socket
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
                    if (data != null) offerPriceNotification(data)

                    intentOfferPrice.putExtra(OFFER_PRICE_TAG, data)
                    sendBroadcast(intentOfferPrice)
                }
            } else sendBroadcast(null)
        }
    }


    private fun changeRideNotification(data: String) {
        val ride = Gson().fromJson(data, Ride::class.java)?.ride

        if (ride != null) {
            ride.statusId?.let { idStep ->
                val statusRide = getByValue(idStep)

                statusRide?.let { status ->
                    //update ride LOCAL
                    ActiveRide.activeRide = ride

                    //update status LOCAL
                    RideStatus.status = status

                    val rideStatusText = getRideStatusText(status)

                    if (rideStatusText != null) {
                        val notification = buildNotification(
                            rideStatusText,
                            "Some subtitle",
                            isSetOngoing = true,
                            isAutoCancel = false,
                            isHeadsUp = isAppClose,
                            isOfferPrice = false
                        )

                        notificatonManager.notify(1, notification)
                    }
                }
            }
        }
    }


    private fun offerPriceNotification(data: String) {
        if (isAppClose) {
            val offer = Gson().fromJson(data, OfferPrice::class.java)?.offerPrice
            val notification = buildNotification(
                "Новое предложение цены!",
                "${offer?.price} руб. - ${offer?.driver?.firstName}",
                isSetOngoing = false,
                isAutoCancel = true,
                isHeadsUp = isAppClose,
                isOfferPrice = true
            )

            notificatonManager.notify(notifyId.getAndIncrement(), notification)
        }
    }


    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int {
        if (!mRunning) {
            mRunning = true

            val status = RideStatus.status
            val rideStatusText = getRideStatusText(status)

            if (rideStatusText != null) {
                val notification = buildNotification(
                    rideStatusText, "Some subtitle",
                    isSetOngoing = true,
                    isAutoCancel = false,
                    isHeadsUp = isAppClose,
                    isOfferPrice = false
                )

                startForeground(1, notification)

                notificatonManager.notify(1, notification)
            }
        }

        return START_NOT_STICKY
    }


    private fun buildNotification(
        title: String,
        content: String,
        isSetOngoing: Boolean,
        isAutoCancel: Boolean,
        isHeadsUp: Boolean,
        isOfferPrice: Boolean
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
            .setAutoCancel(isAutoCancel)
            .setOngoing(isSetOngoing)

        if (isOfferPrice) {
//            notification
//                .addAction(0, "Принять", pendingIntent)
//                .addAction(R.drawable.ic_cross, "Отклонить", pendingIntent)
        }

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

        getDriverInteractor.disconnectSocket()
    }


    private fun getByValue(status: Int) = StatusRide.values().firstOrNull { it.status == status }


    private fun getRideStatusText(status: StatusRide): String? {
        return when (status) {
            StatusRide.SEARCH -> getString(R.string.searchingDriver)
            StatusRide.WAIT_FOR_DRIVER -> getString(R.string.driverInWay)
            StatusRide.WAIT_FOR_PASSANGER -> getString(R.string.driverArrived)
            StatusRide.IN_WAY -> getString(R.string.inWay)
            StatusRide.GET_PLACE -> getString(R.string.youGetPlace).plus("!")
            StatusRide.CANCEL -> getString(R.string.rideCancel)
        }
    }
}