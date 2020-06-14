package bonch.dev.data.repository.passenger.getdriver

import android.util.Log
import bonch.dev.App
import bonch.dev.R
import bonch.dev.data.network.passenger.GetDriverService
import bonch.dev.data.repository.common.ride.Autocomplete
import bonch.dev.data.repository.common.ride.Geocoder
import bonch.dev.domain.entities.common.ride.Ride
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.passenger.getdriver.*
import bonch.dev.domain.interactor.passenger.getdriver.GeocoderHandler
import bonch.dev.domain.interactor.passenger.getdriver.NewDriver
import bonch.dev.domain.interactor.passenger.getdriver.SuggestHandler
import bonch.dev.presentation.interfaces.DataHandler
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class GetDriverRepository : IGetDriverRepository {

    private var service: GetDriverService = App.appComponent
        .getNetworkModule()
        .create(GetDriverService::class.java)

    private var autocomplete: Autocomplete? = null
    private var geocoder: Geocoder? = null


    init {
        geocoder = Geocoder()
    }


    //Geocoder
    override fun requestGeocoder(point: Point, callback: GeocoderHandler) {
        geocoder?.request(point) { address, responsePoint ->
            callback(address, responsePoint)
        }
    }


    //Suggest (autocomplete)
    override fun requestSuggest(
        query: String,
        userLocationPoint: Point?,
        callback: SuggestHandler
    ) {
        if (autocomplete == null) {
            autocomplete = Autocomplete(
                userLocationPoint
            )
        }

        autocomplete?.requestSuggest(query) { suggest ->
            callback(suggest)
        }
    }


    override fun createRide(
        rideInfo: RideInfo,
        token: String,
        callback: DataHandler<RideInfo?>
    ) {
        var response: Response<Ride>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.createRide(
                    headers,
                    rideInfo
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        //Success
                        val rideId = response.body()?.ride?.rideId
                        if (rideId != null) {
                            callback(response.body()?.ride, null)
                        } else {
                            Log.e("CREATE_RIDE", "Create Ride on server failed (rideId null)")
                            callback(null, response.message())
                        }
                    } else {
                        //Error
                        Log.e(
                            "CREATE_RIDE",
                            "Create Ride on server failed with code: ${response.code()}"
                        )
                        callback(null, response.message())
                    }
                }


            } catch (err: Exception) {
                //Error
                callback(null, err.message)
                Log.e("CREATE_RIDE", "${err.printStackTrace()}")
            }
        }
    }


    override fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: DataHandler<String?>
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
        driverId: Int,
        rideId: Int,
        token: String,
        callback: DataHandler<String?>
    ) {
        var response: Response<*>

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //set headers
                val headers = hashMapOf<String, String>()
                headers["Authorization"] = "Bearer $token"

                response = service.linkDriverToRide(headers, rideId, driverId)

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
    override fun getNewDriver(callback: NewDriver) {
        val driver = if (i % 2 == 0) {
            Driver("Костя $i", "Hyundai Solaris", "DF456S", 4.3, R.drawable.ava, 344 + i * 9)
        } else {
            Driver("Александр $i", "Kia Rio", "AR432V", 3.9, R.drawable.ava1, 412 + i * 5)
        }

        if (i < 17)
            callback(driver)

        i++
    }
}