package bonch.dev.data.repository.common.profile

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.common.ProfileService
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.interactor.common.profile.ProfileDataHandler
import bonch.dev.domain.interactor.common.profile.ProfileHandler
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
        id: Int,
        token: String,
        profileData: Profile,
        callback: ProfileHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.saveProfile(headers, id, profileData)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(null)
                    } else {
                        //Error
                        Log.e(
                            "SEND_NAMES",
                            "Send names to server failed with code: ${response.code()}"
                        )
                        callback(response.code().toString())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("SEND_NAMES", "${err.printStackTrace()}")
                callback(err.message)
            }
        }
    }


    override fun getProfile(
        id: Int,
        token: String,
        callback: ProfileDataHandler<Profile?>
    ) {
        var response: Response<Profile>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.getProfile(headers, id)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val profile = response.body()
                        if(profile != null){
                            callback(profile, null)
                        }else{
                            //Error
                            callback(null, response.code().toString())
                        }
                    } else {
                        //Error
                        Log.e(
                            "GET_PROFILE",
                            "Send names to server failed with code: ${response.code()}"
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