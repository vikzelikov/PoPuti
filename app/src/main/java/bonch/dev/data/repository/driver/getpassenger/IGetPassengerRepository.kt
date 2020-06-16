package bonch.dev.data.repository.driver.getpassenger

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

interface IGetPassengerRepository {

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )

    fun linkDriverToRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )

    fun getNewOrders(callback: DataHandler<ArrayList<RideInfo>?>)

}