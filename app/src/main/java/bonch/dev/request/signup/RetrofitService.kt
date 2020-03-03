package bonch.dev.request.signup


import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded
    @POST("/api/auth")
    suspend fun sendPhone(@Field("phone") phone: String): Response<*>
}