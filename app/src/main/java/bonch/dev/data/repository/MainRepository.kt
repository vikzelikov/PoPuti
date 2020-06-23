package bonch.dev.data.repository

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.common.RideService
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainRepository : IMainRepository {

    private var service: RideService = App.appComponent
        .getNetworkModule()
        .create(RideService::class.java)

    override fun getRide(rideId: Int, callback: DataHandler<RideInfo?>) {
        var response: Response<RideInfo>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.getRide(rideId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val rideInfo = response.body()
                        if (rideInfo != null) {
                            //Success
                            callback(response.body(), null)
                        } else {
                            //Error
                            callback(null, "${response.code()}")
                        }
                    } else {
                        //Error
                        Log.e(
                            "CREATE_DRIVER",
                            "Create driver with server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("CREATE_DRIVER", "${err.printStackTrace()}")
                callback(null, "${err.printStackTrace()}")
            }
        }
    }

    override fun connectSocket(rideId: Int, token: String, callback: SuccessHandler) {}

    override fun disconnectSocket() {}

    override fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
    }

    override fun setDriverInRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    ) {
    }

    override fun sendCancelReason(
        rideId: Int,
        textReason: String,
        token: String,
        callback: SuccessHandler
    ) {
    }
}