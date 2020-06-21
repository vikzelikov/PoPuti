package bonch.dev.data.repository.driver.getpassenger

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

interface IGetPassengerRepository {

    fun connectSocket(rideId: Int, token: String, callback: SuccessHandler)

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun disconnectSocket()

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )

    fun setDriverInRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )

    fun offerPrice(price: Int, rideId: Int, userId: Int, token: String, callback: SuccessHandler)

    fun getNewOrders(callback: DataHandler<ArrayList<RideInfo>?>)

}