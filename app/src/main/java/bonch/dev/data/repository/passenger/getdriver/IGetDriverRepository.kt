package bonch.dev.data.repository.passenger.getdriver

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.interactor.passenger.getdriver.GeocoderHandler
import bonch.dev.domain.interactor.passenger.getdriver.NewDriver
import bonch.dev.domain.interactor.passenger.getdriver.SuggestHandler
import bonch.dev.presentation.interfaces.DataHandler
import com.yandex.mapkit.geometry.Point

interface IGetDriverRepository {

    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?, callback: SuggestHandler)

    fun getNewDriver(callback: NewDriver)

    fun createRide(rideInfo: RideInfo, token: String, callback: DataHandler<RideInfo?>)

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: DataHandler<String?>
    )

    fun linkDriverToRide(
        driverId: Int,
        rideId: Int,
        token: String,
        callback: DataHandler<String?>
    )

}