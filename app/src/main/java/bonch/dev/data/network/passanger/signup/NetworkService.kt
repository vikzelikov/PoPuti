package bonch.dev.data.network.passanger.signup


import bonch.dev.domain.entities.passanger.signup.Token
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface NetworkService {

    @FormUrlEncoded
    @POST("/api/auth")
    suspend fun getCode(@Field("phone") phone: String): Response<*>

    @FormUrlEncoded
    @POST("/api/auth/login")
    suspend fun checkCode(@Field("phone") phone: String, @Field("code") code: String): Response<Token>

}