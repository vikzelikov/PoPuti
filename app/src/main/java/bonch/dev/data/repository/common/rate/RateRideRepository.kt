package bonch.dev.data.repository.common.rate

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.common.RideService
import bonch.dev.domain.entities.common.rate.Review
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.interfaces.SuccessHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RateRideRepository : IRateRideRepository {

    private var service: RideService = App.appComponent
        .getNetworkModule()
        .create(RideService::class.java)

    override fun sendReview(review: Review, token: String, callback: SuccessHandler) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                if(review.text == null) review.text = ""

                response = service.sendReview(headers, review)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e("SEND_REVIEW", " ${response.code()}")
                        callback(false)
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("SEND_REVIEW", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }
}