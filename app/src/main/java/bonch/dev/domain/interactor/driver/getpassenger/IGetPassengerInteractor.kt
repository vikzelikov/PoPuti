package bonch.dev.domain.interactor.driver.getpassenger

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler


interface IGetPassengerInteractor {

    fun setDriverInRide(callback: SuccessHandler)

    fun updateRideStatus(status: StatusRide, callback: SuccessHandler)

    fun getNewOrder(callback: DataHandler<ArrayList<RideInfo>?>)

}