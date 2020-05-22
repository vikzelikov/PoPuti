package bonch.dev.data.repository.driver.getpassanger

import android.util.Log
import bonch.dev.App
import bonch.dev.R
import bonch.dev.data.network.driver.GetPassangerService
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.entities.passanger.getdriver.AddressPoint
import bonch.dev.domain.interactor.driver.getpassanger.NewOrder
import bonch.dev.domain.interactor.passanger.getdriver.CommonHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class GetPassangerRepository : IGetPassangerRepository {

    private var service: GetPassangerService = App.appComponent
        .getNetworkModule()
        .create(GetPassangerService::class.java)


    override fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: CommonHandler<String?>
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.updateRideStatus(headers, rideId, status.status)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(null, null)
                    } else {
                        //Error
                        Log.e(
                            "UPDATE_RIDE",
                            "Update ride on server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("UPDATE_RIDE", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    override fun linkDriverToRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: CommonHandler<String?>
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.linkDriverToRide(headers, rideId, userId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        callback(null, null)
                    } else {
                        //Error
                        Log.e(
                            "LINK_DRIVER_TO_RIDE",
                            "Link driver to ride on server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }

            } catch (err: Exception) {
                //Error
                Log.e("LINK_DRIVER_TO_RIDE", "${err.printStackTrace()}")
                callback(null, err.message)
            }
        }
    }


    var i = 0
    override fun getNewOrder(callback: NewOrder) {
        val order = if (i % 2 == 0) {
            Order(
                i,
                "Костя $i",
                R.drawable.ava,
                344 + i * 9,
                4.3,
                "Метро площадь Ленина, Финляндский вокзал, Санкт-Петербург",
                "Парнас Сити",
                12.2 + i * 9,
                "Со мной будет собака, а еще можно проветрить автомобиль до моего приезда пожалуйта хочу ехать в проветренном автомобиле",
                59.953859, 30.354942,
                60.065902, 30.339715

            )
        } else {
            Order(
                i,
                "Александр $i",
                R.drawable.ava1,
                412 + i * 5,
                3.9,
                "Дворцовая площадь, Palace Square, Saint Petersburg",
                "ТРЦ Галерея, Лиговский проспект",
                3.4 + i * 9,
                null,
                59.938738, 30.314848,
                59.928005, 30.360806
            )
        }

        if (i < 17)
            callback(order)

        i++
    }

}