package bonch.dev.poputi.data.repository.common.ride

import android.util.Log
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.common.RideService
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RideRepository : IRideRepository {

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
                            "GET_RIDE",
                            "Get ride from server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("GET_RIDE", "${err.printStackTrace()}")
                callback(null, "${err.printStackTrace()}")
            }
        }
    }
}