package bonch.dev.data.repository.passenger.getdriver

import bonch.dev.data.repository.IMainRepository
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.GeocoderHandler
import bonch.dev.presentation.interfaces.SuggestHandler
import com.yandex.mapkit.geometry.Point

interface IGetDriverRepository : IMainRepository {

    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?, callback: SuggestHandler)

    fun createRide(rideInfo: RideInfo, token: String, callback: DataHandler<RideInfo?>)

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun subscribeOnOfferPrice(callback: DataHandler<String?>)

}