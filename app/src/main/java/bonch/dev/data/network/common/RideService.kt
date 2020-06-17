package bonch.dev.data.network.common

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

}