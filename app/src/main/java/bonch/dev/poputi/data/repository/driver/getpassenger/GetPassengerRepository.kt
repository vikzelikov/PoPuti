package bonch.dev.poputi.data.repository.driver.getpassenger

import android.util.Log
import bonch.dev.poputi.domain.utils.Constants
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.driver.GetPassangerService
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.OfferPrice
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionState
import com.pusher.client.util.HttpAuthorizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class GetPassengerRepository : IGetPassengerRepository {

    private var service: GetPassangerService = App.appComponent
        .getNetworkModule()
        .create(GetPassangerService::class.java)

    private var channel: PrivateChannel? = null
    private var pusher: Pusher? = null


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

            pusher?.connect()

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


    override fun subscribeOnGetOffers(callback: DataHandler<String?>) {
        val offerPriceEvent = "App\\Events\\RideCreate"

        channel?.bind(offerPriceEvent, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                if (event != null) {
                    callback(event.data, null)
                } else {
                    callback(null, "error")
                }
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {}

            override fun onSubscriptionSucceeded(channelName: String?) {}
        })
    }


    override fun subscribeOnChangeRide(callback: DataHandler<String?>) {
        val rideChangeEvent = "App\\Events\\RideChange"

        channel?.bind(rideChangeEvent, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                if (event != null) {
                    Log.e("TEST", "еу")
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

            override fun onAuthenticationFailure(message: String?, e: Exception?) {}

            override fun onSubscriptionSucceeded(channelName: String?) {}
        })
    }


    override fun disconnectSocket() {
        Log.i("SOCKET_PUSHER/P", "DISCONNECT")
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


    override fun offerPrice(
        price: Int,
        rideId: Int,
        userId: Int,
        token: String,
        callback: DataHandler<Offer?>
    ) {
        var response: Response<OfferPrice>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.offerPrice(
                    headers,
                    price,
                    rideId,
                    userId
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val offer = response.body()?.offerPrice
                        if (offer != null) callback(offer, null)
                        else callback(null, "Error")
                    } else {
                        //Error
                        Log.e(
                            "OFFER_PRICE",
                            "Offer price for ride on server failed with code: ${response.code()}"
                        )
                        callback(null, " ${response.code()}")
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("OFFER_PRICE", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    override fun getNewOrders(callback: DataHandler<ArrayList<RideInfo>?>) {
        var response: Response<ArrayList<RideInfo>>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set status in search (free rides)
                response = service.getOrders(StatusRide.SEARCH.status)

                withContext(Dispatchers.Main) {

                    if (response.isSuccessful) {
                        //Success
                        val orders = response.body()
                        if (orders != null) {
                            callback(orders, null)
                        } else {
                            //Error
                            Log.e("GET_ORDERS", " ${response.code()}")
                            callback(null, response.code().toString())
                        }
                    } else {
                        //Error
                        Log.e("GET_ORDERS", " ${response.code()}")
                        callback(null, response.code().toString())
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("GET_ORDERS", "${err.printStackTrace()}")
                callback(null, err.message)
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

                response = service.sendReason(headers, rideId, 0, textReason)

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