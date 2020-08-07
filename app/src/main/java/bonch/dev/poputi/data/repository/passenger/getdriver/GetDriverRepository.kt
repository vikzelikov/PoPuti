package bonch.dev.poputi.data.repository.passenger.getdriver

import android.util.Log
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.passenger.GetDriverService
import bonch.dev.poputi.data.repository.common.ride.Autocomplete
import bonch.dev.poputi.data.repository.common.ride.Geocoder
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.Ride
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.GeocoderHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.interfaces.SuggestHandler
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
                if (rideInfo.city == null) rideInfo.city = "Cанкт-Петербург"
                //todo
                response = service.createRide(
                    headers,
                    rideInfo
                )

                withContext(Dispatchers.Main) {
                    Log.e("TEST","1")
                    if (response.isSuccessful) {
                        Log.e("TEST","2")

                        //Success
                        val rideId = response.body()?.ride?.rideId
                        if (rideId != null) {
                            callback(response.body()?.ride, null)
                        } else {
                            Log.e("CREATE_RIDE", "Create Ride on server failed (rideId null)")
                            callback(null, response.message())
                        }
                    } else {
                        Log.e("TEST","3")

                        //Error
                        Log.e(
                            "CREATE_RIDE",
                            "Create Ride on server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }


            } catch (err: Exception) {
                Log.e("TEST","4")

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


    override fun subscribeOnDeleteOffer(callback: DataHandler<String?>) {
        val offerPriceEvent = "App\\Events\\CancelOffer"

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
        Log.w("SOCKET_PUSHER/P", "SOCKET DISCONNECTED")
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
        price: Int,
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
                    StatusRide.WAIT_FOR_DRIVER.status,
                    price
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

    override fun setDriverInRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
        //not implement
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

                response = service.sendReason(headers, rideId, 1, textReason)

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


    override fun getOffers(rideId: Int, callback: DataHandler<ArrayList<Offer>?>) {
        var response: Response<ArrayList<Offer>>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.getOffers(rideId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val offers = response.body()
                        if (offers != null) callback(offers, null)
                        else callback(null, "error")
                    } else {
                        //Error
                        Log.e("GET_OFFERS", "${response.code()}")
                        callback(null, "${response.code()}")
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("GET_OFFERS", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    override fun deleteOffer(offerId: Int, token: String, callback: SuccessHandler) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.deleteOffer(headers, offerId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e("DELETE_OFFER", "${response.code()}")
                        callback(false)
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("DELETE_OFFER", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }
}