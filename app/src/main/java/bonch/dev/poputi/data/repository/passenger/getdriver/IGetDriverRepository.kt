package bonch.dev.poputi.data.repository.passenger.getdriver

import bonch.dev.poputi.data.repository.IMainRepository
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.passenger.regular.ride.DateInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.GeocoderHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.interfaces.SuggestHandler
import com.yandex.mapkit.geometry.Point

interface IGetDriverRepository : IMainRepository {

    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?, callback: SuggestHandler)

    fun createRide(rideInfo: RideInfo, token: String, callback: DataHandler<RideInfo?>)

    fun createRideSchedule(dateInfo: DateInfo, token: String, callback: SuccessHandler)

    fun subscribeOnOfferPrice(callback: DataHandler<String?>)

    fun getOffers(rideId: Int, callback: DataHandler<ArrayList<Offer>?>)

    fun setDriverInRide(
        userId: Int,
        rideId: Int,
        price: Int,
        token: String,
        callback: SuccessHandler
    )

}