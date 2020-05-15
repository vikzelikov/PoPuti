package bonch.dev.data.network.common

import bonch.dev.domain.entities.common.profile.Profile
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {

    @PUT("/api/users/{id}")
    suspend fun saveProfile(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int,
        @Body profile: Profile
    ): Response<*>


    @GET("/api/users/{id}")
    suspend fun getProfile(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int
    ): Response<Profile>

}