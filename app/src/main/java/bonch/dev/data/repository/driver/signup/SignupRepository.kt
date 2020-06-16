package bonch.dev.data.repository.driver.signup

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.driver.SignupService
import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.DriverDataDTO
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SignupRepository : ISignupRepository {

    private var service: SignupService = App.appComponent
        .getNetworkModule()
        .create(SignupService::class.java)


    override fun createDriver(
        driverData: DriverData,
        token: String,
        userId: Int,
        callback: DataHandler<DriverDataDTO?>
    ) {
        var response: Response<DriverDataDTO>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                driverData.userId = userId

                response = service.createDriver(headers, driverData)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val driverId = response.body()?.driver?.driverId
                        if (driverId != null) {
                            //Success
                            callback(response.body(), null)
                        } else {
                            //Error
                            callback(null, "${response.code()}")
                        }
                    } else {
                        //Error
                        Log.e(
                            "CREATE_DRIVER",
                            "Create driver with server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("CREATE_DRIVER", "${err.printStackTrace()}")
                callback(null, "Error")
            }
        }
    }


    override fun getDriver(
        driverId: Int,
        token: String,
        callback: DataHandler<DriverData?>
    ) {
        var response: Response<DriverData>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.getDriver(headers, driverId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val id = response.body()?.driverId
                        if (id != null) {
                            //Success
                            callback(response.body(), null)
                        } else {
                            //Error
                            callback(null, "${response.code()}")
                        }
                    } else {
                        ///Error
                        Log.e(
                            "GET_DRIVER",
                            "Get driver from server failed with code: ${response.code()}"
                        )
                        callback(null, "${response.code()}")
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("GET_DRIVER", "${err.printStackTrace()}")
                callback(null, "Error")
            }
        }
    }


    override fun putNewPhoto(
        photo: NewPhoto,
        token: String,
        driverId: Int,
        callback: SuccessHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = NetworkUtil.getHeaders(token)

                response = service.putNewPhoto(headers, driverId, photo)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        callback(true)
                    } else {
                        ///Error
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


}