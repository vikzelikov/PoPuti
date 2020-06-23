package bonch.dev.data.network.common

import bonch.dev.domain.entities.common.rate.Review
import bonch.dev.domain.entities.common.ride.RideInfo
import retrofit2.Response
import retrofit2.http.*

interface RideService {

    @FormUrlEncoded
    @PUT("/api/rides/{id}")
    suspend fun updateRideStatus(
        @HeaderMap headers: Map<String, String>,
        @Path("id") rideId: Int,
        @Field("status_id") statusId: Int
    ): Response<*>


    @FormUrlEncoded
    @PUT("/api/rides/{id}")
    suspend fun setDriverInRide(
        @HeaderMap headers: Map<String, String>,
        @Path("id") rideId: Int,
        @Field("driver_id") driverId: Int,
        @Field("status_id") statusId: Int
    ): Response<*>


    @GET("/api/rides/{id}")
    suspend fun getRide(
        @Path("id") rideId: Int
    ): Response<RideInfo>


    @FormUrlEncoded
    @POST("/api/reasons")
    suspend fun sendReason(
        @HeaderMap headers: Map<String, String>,
        @Field("ride_id") rideId: Int,
        @Field("by_passenger") byPassenger: Int,
        @Field("text") text: String
    ): Response<*>


    @POST("/api/reviews")
    suspend fun sendReview(
        @HeaderMap headers: Map<String, String>,
        @Body review: Review
    ): Response<*>

}