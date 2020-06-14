package bonch.dev.domain.interactor.driver.getpassenger

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler


interface IGetPassengerInteractor {

    fun linkDriverToRide()

    fun updateRideStatus(status: StatusRide)

    fun getNewOrder(callback: DataHandler<List<RideInfo>>)

}