package bonch.dev.data.network.passenger

import bonch.dev.data.network.common.RideService
import bonch.dev.domain.entities.common.ride.Ride
import bonch.dev.domain.entities.common.ride.RideInfo
import retrofit2.Response
import retrofit2.http.*

interface GetDriverService : RideService {

    @POST("/api/rides")
    suspend fun createRide(
        @HeaderMap headers: Map<String, String>,
        @Body ride: RideInfo
    ): Response<Ride>

}