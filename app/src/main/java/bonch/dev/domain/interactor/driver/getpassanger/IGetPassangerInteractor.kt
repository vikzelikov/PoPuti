package bonch.dev.domain.interactor.driver.getpassanger

import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.driver.getpassanger.Order

typealias NewOrder = (order: Order) -> Unit

interface IGetPassangerInteractor {

    fun linkDriverToRide()

    fun updateRideStatus(status: StatusRide)

    fun getNewOrder(callback: NewOrder)

}