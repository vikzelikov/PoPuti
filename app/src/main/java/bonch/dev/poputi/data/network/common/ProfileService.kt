package bonch.dev.poputi.data.network.common

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.ProfilePhoto
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

}