package bonch.dev.domain.interactor.driver.getpassanger

import bonch.dev.data.repository.driver.getpassanger.IGetPassangerRepository
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import javax.inject.Inject

class GetPassangerInteractor : IGetPassangerInteractor {

    @Inject
    lateinit var getPassangerRepository: IGetPassangerRepository


    init {
        GetPassangerComponent.getPassangerComponent?.inject(this)
    }


    override fun getNewOrder(callback: NewOrder) {
        getPassangerRepository.getNewOrder{
            callback(it)
        }
    }

}