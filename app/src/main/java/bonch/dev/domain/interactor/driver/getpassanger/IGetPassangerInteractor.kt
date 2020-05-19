package bonch.dev.domain.interactor.driver.getpassanger

import bonch.dev.domain.entities.driver.getpassanger.Order

typealias NewOrder = (order: Order) -> Unit

interface IGetPassangerInteractor {

    fun getNewOrder(callback: NewOrder)

}