package bonch.dev.data.repository.passenger.getdriver

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.passenger.GetDriverService
import bonch.dev.data.repository.common.ride.Autocomplete
import bonch.dev.data.repository.common.ride.Geocoder
import bonch.dev.domain.entities.common.ride.Ride
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.GeocoderHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.interfaces.SuggestHandler
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class GetDriverRepository : IGetDriverRepository {

    private var service: GetDriverService = App.appComponent
        .getNetworkModule()
        .create(GetDriverService::class.java)

    private var autocomplete: Autocomplete? = null
    private var geocoder: Geocoder? = null
    private var channel: PrivateChannel? = null
    private var pusher: Pusher? = null


    init {
        geocoder = Geocoder()
    }


    //Geocoder
    override fun requestGeocoder(point: Point, callback: GeocoderHandler) {
        geocoder?.request(point) { address, responsePoint ->
            callback(address, responsePoint)
        }
    }


    //Suggest (autocomplete)
    override fun requestSuggest(
        query: String,
        userLocationPoint: Point?,
        callback: SuggestHandler
    ) {
        if (autocomplete == null) {
            autocomplete = Autocomplete(
                userLocationPoint
            )
        }

        autocomplete?.requestSuggest(query) { suggest ->
            callback(suggest)
        }
    }


    override fun createRide(
        rideInfo: RideInfo,
        token: String,
        callback: DataHandler<RideInfo?>
    ) {
        var response: Response<Ride>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                if (rideInfo.comment == null) rideInfo.comment = ""
                response = service.createRide(
                    headers,
                    rideInfo
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val rideId = response.body()?.ride?.rideId
                        if (rideId != null) {
                            callback(response.body()?.ride, null)
                        } else {
                            Log.e("CREATE_RIDE", "Create Ride on server failed (rideId null)")
                            callback(null, response.message())
                        }
                    } else {
                        //Error
                        Log.e(
                            "CREATE_RIDE",
                            "Create Ride on server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }


            } catch (err: Exception) {
                //Error
                callback(null, err.message)
                Log.e("CREATE_RIDE", "${err.printStackTrace()}")
            }
        }
    }


    override fun connectSocket(
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
        val log = "SOCKET_PUSHER/P"
        val apiKey = "4f8625f09b5081d92386"
        val channelName = "ride"

        val options = PusherOptions()
        options.setCluster("mt1")
        val auth = HttpAuthorizer(Constants.BASE_URL.plus("/broadcasting/auth"))
        auth.setHeaders(NetworkUtil.getHeaders(token))
        options.authorizer = auth

        if (pusher?.connection?.state != ConnectionState.CONNECTED) {

            pusher = Pusher(apiKey, options)

            pusher?.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {}

                override fun onError(message: String, code: String, e: Exception) {
                    callback(false)
                }
            }, ConnectionState.ALL)

            channel = pusher?.subscribePrivate("private-$channelName.$rideId",
                object : PrivateChannelEventListener {
                    override fun onEvent(event: PusherEvent?) {}

                    override fun onAuthenticationFailure(mess: String?, e: java.lang.Exception?) {
                        Log.e(log, String.format("SOCKET CONNECT FAIL [%s], [%s]", mess, e))
                        callback(false)
                    }

                    override fun onSubscriptionSucceeded(channelName: String?) {
                        Log.i(log, "SOCKET CONNECT SUCCESS")
                        callback(true)
                    }
                })
        }
    }


    override fun subscribeOnChangeRide(callback: DataHandler<String?>) {
        val rideChangeEvent = "App\\Events\\RideChange"

        channel?.bind(rideChangeEvent, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                if (event != null) {
                    callback(event.data, null)
                } else {
                    callback(null, "error")
                }
            }

            override fun onAuthenticationFailure(message: String?, e: java.lang.Exception?) {}

            override fun onSubscriptionSucceeded(channelName: String?) {}
        })
    }


    override fun subscribeOnOfferPrice(callback: DataHandler<String?>) {
        val offerPriceEvent = "App\\Events\\PriceOffer"

        channel?.bind(offerPriceEvent, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                if (event != null) {
                    callback(event.data, null)
                } else {
                    callback(null, "error")
                }
            }

            override fun onAuthenticationFailure(message: String?, e: java.lang.Exception?) {}

            override fun onSubscriptionSucceeded(channelName: String?) {}
        })
    }


    override fun disconnectSocket() {
        pusher?.disconnect()
    }


    override fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.updateRideStatus(headers, rideId, status.status)


                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "UPDATE_RIDE",
                            "Update ride on server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("UPDATE_RIDE", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun setDriverInRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.setDriverInRide(
                    headers,
                    rideId,
                    userId,
                    StatusRide.WAIT_FOR_DRIVER.status
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "LINK_DRIVER_TO_RIDE",
                            "Link driver to ride on server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("LINK_DRIVER_TO_RIDE", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun sendCancelReason(
        rideId: Int,
        textReason: String,
        token: String,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.sendReason(headers, rideId, true, textReason)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e("SEND_REASON", " ${response.code()}")
                        callback(false)
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("SEND_REASON", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun getRide(rideId: Int, callback: DataHandler<RideInfo?>) {}

}