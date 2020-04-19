package bonch.dev.presenter.driver.getpassanger

import bonch.dev.presenter.BasePresenter
import bonch.dev.ContractInterface

class OrdersPresenter : BasePresenter<ContractInterface.IView>(), ContractInterface.IPresenter {



    override fun showBottomSheet() {
        println("Show bottom sheet")

        println(getView())
    }
}