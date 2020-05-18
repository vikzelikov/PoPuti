package bonch.dev.data.repository.passanger.getdriver

import bonch.dev.domain.entities.passanger.getdriver.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.interactor.passanger.getdriver.CommonHandler
import bonch.dev.domain.interactor.passanger.getdriver.GeocoderHandler
import bonch.dev.domain.interactor.passanger.getdriver.NewDriver
import bonch.dev.domain.interactor.passanger.getdriver.SuggestHandler
import com.yandex.mapkit.geometry.Point

interface IGetDriverRepository {

    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?, callback: SuggestHandler)

    fun getNewDriver(callback: NewDriver)

    fun createRide(rideInfo: RideInfo, token: String, callback: CommonHandler<RideInfo?>)

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: CommonHandler<String?>
    )

    fun linkDriverToRide(
        driverId: Int,
        rideId: Int,
        token: String,
        callback: CommonHandler<String?>
    )

}