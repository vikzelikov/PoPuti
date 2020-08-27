package bonch.dev.poputi.data.network.driver

import bonch.dev.poputi.domain.entities.common.rate.Review
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface RatingService {

    @GET("/api/users/me/reviews")
    suspend fun getRating(
        @HeaderMap headers: Map<String, String>
    ): Response<ArrayList<Review>>

}