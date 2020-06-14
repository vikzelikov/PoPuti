package bonch.dev.data.repository.driver.getpassenger

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler

interface IGetPassangerRepository {

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: DataHandler<String?>
    )

    fun linkDriverToRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: DataHandler<String?>
    )

    fun getNewOrders(callback: DataHandler<List<RideInfo>?>)

}