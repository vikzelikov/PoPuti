package bonch.dev.data.repository.driver.getpassanger

import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.interactor.driver.getpassanger.NewOrder
import bonch.dev.domain.interactor.passanger.getdriver.CommonHandler

interface IGetPassangerRepository {

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: CommonHandler<String?>
    )

    fun linkDriverToRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: CommonHandler<String?>
    )

    fun getNewOrder(callback: NewOrder)

}