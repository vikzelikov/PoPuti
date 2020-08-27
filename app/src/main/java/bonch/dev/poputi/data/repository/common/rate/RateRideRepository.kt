package bonch.dev.poputi.data.repository.common.rate

import android.util.Log
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.common.RideService
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
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


    override fun getRating(token: String, callback: DataHandler<ArrayList<Review>?>) {
        var response: Response<ArrayList<Review>>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val headers = NetworkUtil.getHeaders(token)

                response = service.getRating(headers)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val rating = response.body()
                        if (rating != null) callback(rating, null)
                        else callback(null, "error")
                    } else {
                        //Error
                        Log.e("GET_RATING", "${response.code()}")
                        callback(null, "${response.code()}")
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("GET_RATING", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }
}