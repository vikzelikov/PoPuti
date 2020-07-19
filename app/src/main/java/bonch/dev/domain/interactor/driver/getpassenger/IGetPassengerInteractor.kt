package bonch.dev.domain.interactor.driver.getpassenger

import bonch.dev.domain.entities.common.ride.Offer
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler


interface IGetPassengerInteractor {

    fun connectSocket(callback: SuccessHandler)

    fun connectChatSocket(callback: SuccessHandler)

    fun subscribeOnGetOffers(callback: DataHandler<String?>)

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun subscribeOnDeleteOffer(callback: DataHandler<String?>)

    fun subscribeOnChat(callback: DataHandler<String?>)

    fun disconnectSocket()

    fun getUserId(): Int

    fun setDriverInRide(callback: SuccessHandler)

    fun saveRideId(rideId: Int)

    fun getRideId(): Int

    fun removeRideId()

    fun saveWaitTimestamp()

    fun getWaitTimestamp(): Long

    fun updateRideStatus(status: StatusRide, callback: SuccessHandler)

    fun offerPrice(price: Int, rideId: Int, callback: DataHandler<Offer?>)

    fun deleteOffer(offerId: Int, callback: SuccessHandler)

    fun getNewOrder(callback: DataHandler<ArrayList<RideInfo>?>)

    fun sendReason(textReason: String, callback: SuccessHandler)

}