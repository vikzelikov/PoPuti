package bonch.dev.data.repository.passanger.signup

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.passanger.signup.NetworkService
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
                callback(false, null)
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }

}
