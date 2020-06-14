package bonch.dev.data.repository.passenger.signup

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.passenger.SignupService
import bonch.dev.domain.entities.common.profile.ProfileData
import bonch.dev.domain.entities.passenger.signup.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignupRepository : ISignupRepository {

    private var service: SignupService = App.appComponent
        .getNetworkModule()
        .create(SignupService::class.java)


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
        var response: Response<Token>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.checkCode(phone, code)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val accessToken = response.body()?.accessToken

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
        var response: Response<ProfileData>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                //send request
                response = service.getUserId(headers)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val id = response.body()?.data?.id
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
}
