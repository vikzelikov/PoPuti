package bonch.dev.poputi.data.repository.passenger.regular.ride

import android.util.Log
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.passenger.RegularRidesService
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RegularRidesRepository : IRegularRidesRepository {


    private var service: RegularRidesService = App.appComponent
        .getNetworkModule()
        .create(RegularRidesService::class.java)


    override fun getActiveRides(token: String, callback: DataHandler<ArrayList<RideInfo>?>) {
        var response: Response<ArrayList<RideInfo>?>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.getActiveRides(headers)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        val regularRides = response.body()
                        if (regularRides != null) {
                            //Success
                            callback(regularRides, null)
                        } else {
                            //Error
                            Log.e(
                                "GET_ACTIVE_REGULAR",
                                "Get regular rides from server failed NULL ALL"
                            )
                            callback(null, "NULL")
                        }
                    } else {
                        //Error
                        Log.e(
                            "GET_ACTIVE_REGULAR",
                            "Get regular rides from server failed with code: ${response.code()}"
                        )
                        callback(null, "${response.code()}")
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("GET_ACTIVE_REGULAR", "${err.printStackTrace()}")
                callback(null, "${err.message}")
            }
        }
    }


    override fun getArchiveRides(token: String, callback: DataHandler<ArrayList<RideInfo>?>) {
        var response: Response<ArrayList<RideInfo>?>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.getArchiveRides(headers)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        val regularRides = response.body()
                        if (regularRides != null) {
                            //Success
                            callback(regularRides, null)
                        } else {
                            //Error
                            Log.e(
                                "GET_ARCHIVE_REGULAR",
                                "Get regular rides from server failed NULL ALL"
                            )
                            callback(null, "NULL")
                        }
                    } else {
                        //Error
                        Log.e(
                            "GET_ARCHIVE_REGULAR",
                            "Get regular rides from server failed with code: ${response.code()}"
                        )
                        callback(null, "${response.code()}")
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("GET_ARCHIVE_REGULAR", "${err.printStackTrace()}")
                callback(null, "${err.message}")
            }
        }
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


    override fun deleteRide(
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.deleteRide(headers, rideId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "DELETE_RIDE",
                            "Delete ride on server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("DELETE_RIDE", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }
}