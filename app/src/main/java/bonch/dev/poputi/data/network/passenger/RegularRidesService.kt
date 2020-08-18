package bonch.dev.poputi.data.network.passenger

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import retrofit2.Response
import retrofit2.http.*

interface RegularRidesService {

    @GET("api/users/me/regular_rides")
    suspend fun getActiveRides(
        @HeaderMap headers: Map<String, String>
    ): Response<ArrayList<RideInfo>?>


    @GET("api/users/me/regular_rides_archived")
    suspend fun getArchiveRides(
        @HeaderMap headers: Map<String, String>
    ): Response<ArrayList<RideInfo>?>


    @FormUrlEncoded
    @PUT("/api/rides/{id}")
    suspend fun updateRideStatus(
        @HeaderMap headers: Map<String, String>,
        @Path("id") rideId: Int,
        @Field("status_id") statusId: Int
    ): Response<*>
}