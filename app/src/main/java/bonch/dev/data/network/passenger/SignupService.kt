package bonch.dev.data.network.passenger


import bonch.dev.domain.entities.common.profile.ProfileData
import bonch.dev.domain.entities.passenger.signup.Token
import retrofit2.Response
import retrofit2.http.*

interface SignupService {

    @FormUrlEncoded
    @POST("/api/auth")
    suspend fun getCode(@Field("phone") phone: String): Response<*>

    @FormUrlEncoded
    @POST("/api/auth/login")
    suspend fun checkCode(@Field("phone") phone: String, @Field("code") code: String): Response<Token>

    @GET("/api/auth/check")
    suspend fun getUserId(@HeaderMap headers: Map<String, String>): Response<ProfileData>

}