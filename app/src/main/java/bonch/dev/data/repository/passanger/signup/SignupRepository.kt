package bonch.dev.data.repository.passanger.signup

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.passanger.signup.NetworkService
import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.data.repository.passanger.profile.pojo.ProfileData
import bonch.dev.domain.entities.passanger.signup.DataSignup
import bonch.dev.domain.entities.passanger.signup.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignupRepository : ISignupRepository {

    private var service: NetworkService = App.appComponent
        .getNetworkModule()
        .create(NetworkService::class.java)


    override fun sendSms(phone: String, callback: SignupHandler) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.getCode(phone)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(null)
                    } else {
                        //Error
                        callback(response.code().toString())
                    }
                }
            } catch (error: Exception) {
                //Error
                callback(error.message)
                Log.e("Retrofit", "${error.printStackTrace()}")
            }
        }
    }


    override fun checkCode(phone: String, code: String, callback: SignupCheckHandler<Boolean>) {
        var response: Response<Token>?

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.checkCode(phone, code)

                withContext(Dispatchers.Main) {
                    if (response != null && response!!.isSuccessful) {
                        val accessToken = response!!.body()?.accessToken

                        accessToken?.let {
                            //Succes
                            callback(true, accessToken)
                        }
                    } else {
                        //Error
                        callback(false, null)
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("Retrofit", "${err.printStackTrace()}")
                callback(false, null)
            }
        }
    }


    override fun getUserId(token: String, callback: SignupCheckHandler<Int?>) {
        var response: Response<ProfileData>?

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val stringStringMap = hashMapOf<String, String>()
                stringStringMap["Authorization"] = "Bearer $token"
                stringStringMap["Content-Type"] = "application/json"
                stringStringMap["Accept"] = "application/json"

                //send request
                response = service.getUserId(stringStringMap)

                withContext(Dispatchers.Main) {
                    if (response != null && response!!.isSuccessful) {
                        //Success
                        val id = response!!.body()?.data?.id
                        callback(id, null)
                    } else {
                        //Error
                        callback(null, null)
                    }
                }
            } catch (err: Exception) {
                //Error
                callback(null, null)
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }


    override fun sendProfileData(id: Int, token: String, profileData: Profile) {
        var response: Response<*>?

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //send request
                if (profileData.firstName != null && profileData.lastName != null) {

                    //set headers
                    val stringStringMap = hashMapOf<String, String>()
                    println(token)
                    stringStringMap["Authorization"] = "Bearer $token"

                    response = service.sendProfileData(
                        stringStringMap,
                        id,
                        profileData.firstName!!,
                        profileData.lastName!!
                    )
                    withContext(Dispatchers.Main) {
                        println(response?.code())
                        if (response != null && response!!.isSuccessful) {
                            //200 OK
                        } else {
                            //Error
                        }
                    }
                }


            } catch (err: Exception) {
                //Error
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }

}
