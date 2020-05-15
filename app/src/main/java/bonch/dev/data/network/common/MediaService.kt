package bonch.dev.data.network.common

import bonch.dev.domain.entities.common.media.Media
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MediaService {

    @Multipart
    @POST("/api/files")
    suspend fun loadPhoto(
        @HeaderMap headers: Map<String, String>,
        @Part image: MultipartBody.Part
    ): Response<Media>

    @DELETE("/api/files/{id}")
    suspend fun deletePhoto(
        @HeaderMap headers: Map<String, String>,
        @Path("id") imageId: Int
    ): Response<*>

}