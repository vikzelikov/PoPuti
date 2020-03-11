package bonch.dev.network.signup


import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("/api/auth")
    suspend fun sendPhone(@Field("phone") phone: String): Response<*>

    @FormUrlEncoded
    @POST("/api/auth/login")
    suspend fun checkCode(@Field("phone") phone: String, @Field("code") code: String): Response<*>

}