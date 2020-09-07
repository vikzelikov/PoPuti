package bonch.dev.poputi.domain.interactor.driver.getpassenger

import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import com.yandex.mapkit.geometry.Point


interface IGetPassengerInteractor {

    fun connectSocketOrders(callback: SuccessHandler)

    fun connectSocket(callback: SuccessHandler)

    fun connectChatSocket(callback: SuccessHandler)

    fun subscribeOnGetOrders(callback: DataHandler<String?>)

    fun subscribeOnDeleteOrder(callback: DataHandler<String?>)

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

    fun cancelRide(textReason: String, rideId: Int)

    fun updateDriverGeo(point: Point)

}