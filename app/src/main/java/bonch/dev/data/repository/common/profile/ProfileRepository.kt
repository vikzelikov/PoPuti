package bonch.dev.data.repository.common.profile

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.common.ProfileService
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.common.profile.ProfilePhoto
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.ErrorHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class ProfileRepository : IProfileRepository {

    private var service: ProfileService = App.appComponent
        .getNetworkModule()
        .create(ProfileService::class.java)


    override fun saveProfile(
        userId: Int,
        token: String,
        profileData: Profile,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                if (profileData.email == null) profileData.email = ""

                response = service.saveProfile(headers, userId, profileData)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "SEND_NAMES",
                            "Send names to server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("SEND_NAMES", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun savePhoto(
        userId: Int,
        token: String,
        photo: ProfilePhoto,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.savePhoto(headers, userId, photo)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "LOAD_PHOTO",
                            "Load photo to server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("LOAD_PHOTO", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun getProfile(
        userId: Int,
        token: String,
        callback: DataHandler<Profile?>
    ) {
        var response: Response<Profile>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.getProfile(headers, userId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val profile = response.body()
                        if (profile != null) {
                            callback(profile, null)
                        } else {
                            //Error
                            callback(null, response.code().toString())
                        }
                    } else {
                        //Error
                        Log.e(
                            "GET_PROFILE",
                            "Get profile from server failed with code: ${response.code()}"
                        )
                        callback(null, response.code().toString())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("GET_PROFILE", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }

}