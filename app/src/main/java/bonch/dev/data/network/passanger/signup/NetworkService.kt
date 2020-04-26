package bonch.dev.data.network.passanger.signup


import bonch.dev.data.repository.passanger.profile.pojo.ProfileData
import bonch.dev.domain.entities.passanger.signup.Token
import retrofit2.Response
import retrofit2.http.*

interface NetworkService {

    @FormUrlEncoded
    @POST("/api/auth")
    suspend fun getCode(@Field("phone") phone: String): Response<*>

    @FormUrlEncoded
    @POST("/api/auth/login")
    suspend fun checkCode(@Field("phone") phone: String, @Field("code") code: String): Response<Token>

    @GET("/api/auth/check")
    suspend fun getUserId(@HeaderMap headers: Map<String, String>): Response<ProfileData>

    @FormUrlEncoded
    @PUT("/api/users/{id}")
    suspend fun sendProfileData(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String
    ): Response<*>

}