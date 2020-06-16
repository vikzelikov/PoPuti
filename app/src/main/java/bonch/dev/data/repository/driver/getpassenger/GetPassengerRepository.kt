package bonch.dev.data.repository.driver.getpassenger

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.driver.GetPassangerService
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class GetPassengerRepository : IGetPassengerRepository {

    private var service: GetPassangerService = App.appComponent
        .getNetworkModule()
        .create(GetPassangerService::class.java)


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


    override fun linkDriverToRide(
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

                response = service.linkDriverToRide(headers, rideId, userId)

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

}