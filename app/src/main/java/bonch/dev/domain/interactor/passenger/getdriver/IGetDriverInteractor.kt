package bonch.dev.domain.interactor.passenger.getdriver

import bonch.dev.domain.entities.common.ride.Address
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.GeocoderHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.interfaces.SuggestHandler
import com.yandex.mapkit.geometry.Point


interface IGetDriverInteractor {

    fun initRealm()

    //Yandex MapKit
    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?, callback: SuggestHandler)


    //Ride
    fun createRide(rideInfo: RideInfo, callback: SuccessHandler)

    fun connectSocket(callback: SuccessHandler)

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun subscribeOnOfferPrice(callback: DataHandler<String?>)

    fun disconnectSocket()

    fun updateRideStatus(status: StatusRide, callback: SuccessHandler)

    fun setDriverInRide(userId: Int, callback: SuccessHandler)

    fun sendReason(textReason: String, callback: SuccessHandler)

    //Cash
    fun getCashRequest(query: String): ArrayList<Address>?

    fun getCashSuggest(): ArrayList<Address>?

    fun saveCashRequest(cashRequest: ArrayList<Address>)

    fun saveCashSuggest(cashAddresses: ArrayList<Address>)

    fun deleteCashSuggest(address: Address)

    fun closeRealm()

}