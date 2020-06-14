package bonch.dev.data.network.driver

import bonch.dev.data.network.common.RideService
import bonch.dev.domain.entities.common.ride.RideInfo
import retrofit2.Response
import retrofit2.http.*

interface GetPassangerService : RideService {

    @GET("/api/rides")
    suspend fun getOrders(
        @Query("status") status: Int
    ): Response<List<RideInfo>>

}