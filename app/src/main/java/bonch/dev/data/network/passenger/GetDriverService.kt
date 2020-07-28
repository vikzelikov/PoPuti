package bonch.dev.data.network.passenger

import bonch.dev.data.network.common.RideService
import bonch.dev.domain.entities.common.ride.Offer
import bonch.dev.domain.entities.common.ride.OfferPrice
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


    @FormUrlEncoded
    @PUT("/api/rides/{id}")
    suspend fun setDriverInRide(
        @HeaderMap headers: Map<String, String>,
        @Path("id") rideId: Int,
        @Field("driver_id") driverId: Int,
        @Field("status_id") statusId: Int,
        @Field("price") price: Int
    ): Response<*>


    @GET("/api/rides/{id}/offers")
    suspend fun getOffers(
        @Path("id") rideId: Int
    ): Response<ArrayList<Offer>>

}