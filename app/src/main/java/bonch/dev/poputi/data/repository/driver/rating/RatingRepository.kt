package bonch.dev.poputi.data.repository.driver.rating

import android.util.Log
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.driver.RatingService
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.interfaces.DataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class RatingRepository : IRatingRepository {

    private var service: RatingService = App.appComponent
        .getNetworkModule()
        .create(RatingService::class.java)


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