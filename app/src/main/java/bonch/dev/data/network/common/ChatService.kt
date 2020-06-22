package bonch.dev.data.network.common

import bonch.dev.domain.entities.common.chat.Message
import retrofit2.Response
import retrofit2.http.*

interface ChatService {

    @POST("/api/messages")
    suspend fun sendMessage(
        @HeaderMap headers: Map<String, String>,
        @Body message: Message
    ): Response<*>


    @GET("/api/rides/{id}/chat")
    suspend fun getMessages(
        @Path("id") rideId: Int
    ): Response<ArrayList<Message>>

}