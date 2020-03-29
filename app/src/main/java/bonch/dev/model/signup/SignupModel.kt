package bonch.dev.model.signup

import android.util.Log
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import bonch.dev.MainActivity
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.model.signup.pojo.Token
import bonch.dev.network.signup.RetrofitService
import bonch.dev.presenter.signup.SignupPresenter
import bonch.dev.utils.Constants.ACCESS_TOKEN
import bonch.dev.utils.Constants.PROFILE_REALM_NAME
import bonch.dev.utils.NetworkUtil
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class SignupModel(private val signupPresenter: SignupPresenter) {

    fun sendSms(phone: String) {
        var service: RetrofitService? = null
        var response: Response<*>

        if (service == null) {
            service = NetworkUtil.makeRetrofitService()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.getCode(phone)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        println(response.message())
                    } else {
                        println(response.code())
                    }
                }
            } catch (err: HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }


    fun checkCode(phone: String, code: String) {
        var service: RetrofitService? = null
        var response: Response<Token>?

        if (service == null) {
            service = NetworkUtil.makeRetrofitService()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                response = service.checkCode(phone, code)

                withContext(Dispatchers.Main) {
                    if (response != null && response!!.isSuccessful) {
                        val accessToken = response!!.body()?.accessToken

                        accessToken?.let {
                            saveToken(it)
                        }

                        signupPresenter.onResponseCheckCode(true)
                    } else {
                        println(response?.code())
                    }
                }
            } catch (err: Exception) {
                signupPresenter.onResponseCheckCode(false)
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }


    private fun saveToken(accessToken: String) {
        val activity = signupPresenter.fragment.activity as MainActivity
        val pref = getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()
        editor.putString(ACCESS_TOKEN, accessToken)
        editor.apply()
    }


    fun saveFullName(profileData: Profile) {
        val context = signupPresenter.fragment.context

        if (context != null) {
            Realm.init(context)
            val config = RealmConfiguration.Builder()
                .name(PROFILE_REALM_NAME)
                .build()
            val realm = Realm.getInstance(config)


            realm?.executeTransactionAsync({ bgRealm ->
                bgRealm.insertOrUpdate(profileData)
            }, {
                realm.close()
                //ok
            }, {
                realm.close()
                //fail
            })
        }
    }
}
