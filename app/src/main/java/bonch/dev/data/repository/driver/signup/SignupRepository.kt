package bonch.dev.data.repository.driver.signup

import android.util.Log
import bonch.dev.App
import bonch.dev.data.network.driver.SignupService
import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.DriverDataDTO
import bonch.dev.domain.entities.driver.signup.NewPhoto
import bonch.dev.domain.interactor.driver.signup.SignupHandler
import bonch.dev.presentation.interfaces.NotificationHandler
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
        callback: SignupHandler<DriverDataDTO?>
    ) {
        var response: Response<DriverDataDTO>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

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
                            callback(null, response.message())
                        }
                    } else {
                        //Error
                        callback(null, response.message())
                        Log.e(
                            "CREATE_DRIVER",
                            "Create driver with server failed with code: ${response.code()}"
                        )
                    }
                }

            } catch (err: Exception) {
                //Error
                callback(null, "Error")
                Log.e("CREATE_DRIVER", "${err.printStackTrace()}")
            }
        }
    }


    override fun getDriver(
        driverId: Int,
        token: String,
        callback: SignupHandler<DriverData?>
    ) {
        var response: Response<DriverData>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.getDriver(headers, driverId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val id = response.body()?.driverId
                        if (id != null) {
                            //Success
                            callback(response.body(), null)
                        } else {
                            //Error
                            callback(null, response.message())
                        }
                    } else {
                        ///Error
                        callback(null, response.message())
                        Log.e(
                            "GET_DRIVER",
                            "Get driver from server failed with code: ${response.code()}"
                        )
                    }
                }

            } catch (err: Exception) {
                //Error
                callback(null, "Error")
                Log.e("GET_DRIVER", "${err.printStackTrace()}")
            }
        }
    }


    override fun putNewPhoto(
        photo: NewPhoto,
        token: String,
        driverId: Int,
        callback: NotificationHandler
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

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