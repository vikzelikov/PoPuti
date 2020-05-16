package bonch.dev.presentation.modules.driver.getpassanger.presenter

import bonch.dev.data.driver.getpassanger.pojo.Order

interface ContractPresenter {

    interface IOrdersPresenter {
        fun onClickItem(order: Order)
        fun instance(): OrdersPresenter
    }


}