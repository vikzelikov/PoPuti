package bonch.dev.poputi.data.repository.common.profile

import android.util.Log
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.App
import bonch.dev.poputi.data.network.common.ProfileService
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.ProfilePhoto
import bonch.dev.poputi.domain.entities.common.profile.verification.NewPhoto
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
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


    override fun putNewPhoto(
        photo: NewPhoto,
        token: String,
        userId: Int,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.putNewPhoto(headers, userId, photo)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        callback(true)
                    } else {
                        //Error
                        Log.e(
                            "PUT_NEW_PHOTO",
                            "Put new photo to server failed with code: ${response.code()}"
                        )
                        callback(false)
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("PUT_NEW_PHOTO", "${err.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun getStoryRidesPassenger(
        token: String,
        callback: DataHandler<ArrayList<RideInfo>?>
    ) {
        var response: Response<ArrayList<RideInfo>>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.getStoryRidesPassenger(headers)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val story = response.body()
                        if (story != null) callback(story, null)
                        else callback(null, "error")

                    } else {
                        //Error
                        Log.e(
                            "GET_STORY_PASSENGER",
                            "Get story from server failed with code: ${response.code()}"
                        )
                        callback(null, response.code().toString())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("GET_STORY_PASSENGER", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    override fun getStoryRidesDriver(token: String, callback: DataHandler<ArrayList<RideInfo>?>) {
        var response: Response<ArrayList<RideInfo>>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.getStoryRidesDriver(headers)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val story = response.body()
                        if (story != null) callback(story, null)
                        else callback(null, "error")

                    } else {
                        //Error
                        Log.e(
                            "GET_STORY_DRIVER",
                            "Get story from server failed with code: ${response.code()}"
                        )
                        callback(null, response.code().toString())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("GET_STORY_DRIVER", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }
}