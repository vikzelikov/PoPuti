package bonch.dev.network.signup


import bonch.dev.model.signup.pojo.Token
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("/api/auth")
    suspend fun getCode(@Field("phone") phone: String): Response<*>

    @FormUrlEncoded
    @POST("/api/auth/login")
    suspend fun checkCode(@Field("phone") phone: String, @Field("code") code: String): Response<Token>

}