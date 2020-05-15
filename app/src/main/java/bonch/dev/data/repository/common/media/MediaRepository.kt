package bonch.dev.data.repository.common.media

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.common.MediaService
import bonch.dev.domain.entities.common.media.Media
import bonch.dev.domain.entities.common.media.MediaObject
import bonch.dev.presentation.interfaces.NotificationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class MediaRepository : IMediaRepository {

    private var service: MediaService = App.appComponent
        .getNetworkModule()
        .create(MediaService::class.java)


    override fun loadPhoto(token: String, image: File, callback: MediaHandlerData<MediaObject?>) {
        var response: Response<Media>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                //set multipart data
                val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), image)
                val body = MultipartBody.Part.createFormData("file", image.name, requestFile)

                response = service.loadPhoto(headers, body)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val imageId = response.body()?.media?.id

                        if (imageId != null) {
                            callback(response.body()?.media, null)
                        } else {
                            //Error
                            Log.e(
                                "LOAD_PHOTO",
                                "Load photo to server failed with data: {${response.body()?.media}}"
                            )
                            callback(null, response.message())
                        }
                    } else {
                        //Error
                        Log.e(
                            "LOAD_PHOTO",
                            "Load photo to server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }


            } catch (err: Exception) {
                //Error
                Log.e("LOAD_PHOTO", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    override fun deletePhoto(token: String, imageId: Int, callback: NotificationHandler) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.deletePhoto(headers, imageId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //ok
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "DELETE_PHOTO",
                            "Delete photo from server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }


            } catch (err: Exception) {
                //Error
                Log.e("DELETE_PHOTO", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }

}