package bonch.dev.poputi.domain.interactor.passenger.getdriver

import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.entities.passenger.regular.ride.DateInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.GeocoderHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.interfaces.SuggestHandler
import com.yandex.mapkit.geometry.Point


interface IGetDriverInteractor {

    fun initRealm()

    //Yandex MapKit
    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?, callback: SuggestHandler)


    //Ride
    fun createRide(rideInfo: RideInfo, callback: SuccessHandler)

    fun updateRide(rideInfo: RideInfo, callback: SuccessHandler)

    fun createRideSchedule(dateInfo: DateInfo, callback: SuccessHandler)

    fun updateRideSchedule(dateInfo: DateInfo, callback: SuccessHandler)

    fun connectSocket(callback: SuccessHandler)

    fun connectChatSocket(callback: SuccessHandler)

    fun connectSocketGetGeoDriver(callback: SuccessHandler)

    fun subscribeOnGetGeoDriver(callback: DataHandler<String?>)

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun subscribeOnOfferPrice(callback: DataHandler<String?>)

    fun subscribeOnDeleteOffer(callback: DataHandler<String?>)

    fun subscribeOnChat(callback: DataHandler<String?>)

    fun disconnectSocket()

    fun saveRideId(rideId: Int)

    fun getRideId(): Int

    fun removeRideId()

    fun updateRideStatus(status: StatusRide, callback: SuccessHandler)

    fun setDriverInRide(userId: Int, price: Int, callback: SuccessHandler)

    fun getOffers(callback: DataHandler<ArrayList<Offer>?>)

    fun deleteOffer(offerId: Int, callback: SuccessHandler)

    fun cancelRide(textReason: String, rideId: Int)

    fun saveBankCard(bankCard: BankCard)

    fun getBankCards(): ArrayList<BankCard>


    //Cash
    fun getCashRequest(query: String): ArrayList<Address>?

    fun getCashSuggest(): ArrayList<Address>?

    fun saveCashRequest(cashRequest: ArrayList<Address>)

    fun saveCashSuggest(cashAddresses: ArrayList<Address>)

    fun deleteCashSuggest(address: Address)

    fun closeRealm()

}