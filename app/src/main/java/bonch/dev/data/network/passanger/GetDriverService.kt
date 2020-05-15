package bonch.dev.data.network.passanger

import bonch.dev.domain.entities.passanger.getdriver.Ride
import bonch.dev.domain.entities.passanger.getdriver.RideInfo
import retrofit2.Response
import retrofit2.http.*

interface GetDriverService {

    @POST("/api/rides")
    suspend fun createRide(
        @HeaderMap headers: Map<String, String>,
        @Body ride: RideInfo
    ): Response<Ride>


    @FormUrlEncoded
    @PUT("/api/rides/{id}")
    suspend fun updateRideStatus(
        @HeaderMap headers: Map<String, String>,
        @Path("id") rideId: Int,
        @Field("status_id") statusId: Int
    ): Response<*>


    @FormUrlEncoded
    @PUT("/api/rides/{id}")
    suspend fun linkDriverToRide(
        @HeaderMap headers: Map<String, String>,
        @Path("id") rideId: Int,
        @Field("driver_id") driverId: Int
    ): Response<*>


}