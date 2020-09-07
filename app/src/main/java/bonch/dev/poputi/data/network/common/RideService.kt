package bonch.dev.poputi.data.network.common

import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
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


    @DELETE("/api/offers/{id}")
    suspend fun deleteOffer(
        @HeaderMap headers: Map<String, String>,
        @Path("id") offerId: Int
    ): Response<*>


    @GET("/api/rides/{id}")
    suspend fun getRide(
        @Path("id") rideId: Int
    ): Response<RideInfo>


    @GET("/api/users/me/reviews")
    suspend fun getRating(
        @HeaderMap headers: Map<String, String>
    ): Response<ArrayList<Review>>


    @FormUrlEncoded
    @PUT("/api/drivers/{id}/location")
    suspend fun updateDriverGeo(
        @HeaderMap headers: Map<String, String>,
        @Path("id") driverId: Int,
        @Field("longitude") longitude: Double,
        @Field("latitude") latitude: Double
    ): Response<*>


}