package bonch.dev.presentation.presenter.driver.getpassanger

import bonch.dev.App
import bonch.dev.presentation.presenter.BasePresenter
import bonch.dev.ContractInterface
import bonch.dev.data.repository.driver.getpassanger.OrdersRepository
import javax.inject.Inject

class OrdersPresenter : BasePresenter<ContractInterface.IView>(), ContractInterface.IPresenter {

    @Inject
    lateinit var ordersRepository: OrdersRepository

    init {
        App.getPassangerComponent?.inject(this)
    }

    override fun showBottomSheet() {
        println("Show bottom sheet")

        println(getView())

        ordersRepository.test()

    }
}