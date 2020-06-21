package bonch.dev.domain.interactor.driver.getpassenger

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler


interface IGetPassengerInteractor {

    fun connectSocket(callback: SuccessHandler)

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun disconnectSocket()

    fun getUserId(): Int

    fun setDriverInRide(callback: SuccessHandler)

    fun saveRideId()

    fun updateRideStatus(status: StatusRide, callback: SuccessHandler)

    fun offerPrice(price: Int, rideId: Int, callback: SuccessHandler)

    fun getNewOrder(callback: DataHandler<ArrayList<RideInfo>?>)

}