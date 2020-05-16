package bonch.dev.presentation.modules.driver.getpassanger.presenter

import bonch.dev.data.driver.getpassanger.pojo.Order
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.driver.getpassanger.view.ContractView


class OrdersPresenter : BasePresenter<ContractView.IOrdersView>(),
    ContractPresenter.IOrdersPresenter {

    override fun onClickItem(order: Order){
        println(order.price)
    }

    override fun instance(): OrdersPresenter {
        return this
    }

}