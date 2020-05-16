package bonch.dev.presentation.modules.driver.getpassanger.presenter

import bonch.dev.domain.entities.driver.getpassanger.Order

interface ContractPresenter {

    interface IOrdersPresenter {
        fun onClickItem(order: Order)
        fun startSearchOrders()
        fun instance(): OrdersPresenter
    }


}