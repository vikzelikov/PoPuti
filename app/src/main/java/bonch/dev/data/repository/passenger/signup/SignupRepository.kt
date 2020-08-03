package bonch.dev.data.repository.passenger.signup

import android.util.Log
import android.widget.Toast
import bonch.dev.App
import bonch.dev.data.network.passenger.SignupService
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.entities.passenger.signup.Token
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.HttpURLConnection

class SignupRepository : ISignupRepository {

    private var service: SignupService = App.appComponent
        .getNetworkModule()
        .create(SignupService::class.java)


    override fun sendSms(phone: String, callback: SuccessHandler) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.getCode(phone)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(true)
                    } else {
                        //Error
                        Log.e("SEND_SMS", "${response.code()}")
                        callback(false)
                    }
                }
            } catch (error: Exception) {
                //Error
                Log.e("SEND_SMS", "${error.printStackTrace()}")
                callback(false)
            }
        }
    }


    override fun checkCode(phone: String, code: String, callback: DataHandler<String?>) {
        var response: Response<Token>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.checkCode(phone, code)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val accessToken = response.body()?.accessToken

                        accessToken?.let {
                            //Succes
                            callback(accessToken, null)
                        }
                    } else {
                        //Error
                        Log.e("CHECK_CODE", "${response.code()}")
                        callback(null, "${response.code()}")
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("CHECK_CODE", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    override fun getUserId(token: String, callback: DataHandler<Int?>) {
        var response: Response<Profile>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                //send request
                response = service.getUserId(headers)

                withContext(Dispatchers.Main) {

                    when (response.code()) {
                        HttpURLConnection.HTTP_OK -> {
                            //Success
                            val id = response.body()?.id

                            if (id != null) callback(id, null)
                            else callback(-1, "${response.body()}")
                        }

                        HttpURLConnection.HTTP_UNAUTHORIZED -> {
                            //go to signup
                            callback(-1, "${response.body()}")
                        }

                        else -> {
                            //Error
                            Log.e("GET_USER_ID", "${response.code()}")
                            callback(null, "${response.code()}")
                        }
                    }
                }
            } catch (err: Exception) {
                //Error
                Log.e("GET_USER_ID", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }
}
