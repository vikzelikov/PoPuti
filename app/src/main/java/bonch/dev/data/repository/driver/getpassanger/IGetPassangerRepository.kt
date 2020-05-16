package bonch.dev.data.repository.driver.getpassanger

import bonch.dev.domain.interactor.driver.getpassanger.NewOrder

interface IGetPassangerRepository {

    fun getNewOrder(callback: NewOrder)

}