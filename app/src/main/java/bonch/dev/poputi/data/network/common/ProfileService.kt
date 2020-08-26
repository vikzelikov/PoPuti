package bonch.dev.poputi.data.network.common

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.ProfilePhoto
import bonch.dev.poputi.domain.entities.common.profile.verification.NewPhoto
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {

    @PUT("/api/users/{id}")
    suspend fun saveProfile(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int,
        @Body profile: Profile
    ): Response<*>


    @PUT("/api/users/{id}")
    suspend fun savePhoto(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int,
        @Body photo: ProfilePhoto
    ): Response<*>


    @GET("/api/users/{id}")
    suspend fun getProfile(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int
    ): Response<Profile>


    @PUT("/api/users/{id}")
    suspend fun putNewPhoto(
        @HeaderMap headers: Map<String, String>,
        @Path("id") driverId: Int,
        @Body photo: NewPhoto
    ): Response<*>


    @GET("/api/users/me/passenger_history")
    suspend fun getStoryRidesPassenger(
        @HeaderMap headers: Map<String, String>
    ): Response<ArrayList<RideInfo>>


    @GET("/api/users/me/driver_history")
    suspend fun getStoryRidesDriver(
        @HeaderMap headers: Map<String, String>
    ): Response<ArrayList<RideInfo>>

}