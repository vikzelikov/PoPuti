package bonch.dev.presentation.modules.driver.getpassanger.view

import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.driver.getpassanger.adapters.OrdersAdapter

interface ContractView {

    interface IOrdersView : IBaseView {
        fun getAdapter(): OrdersAdapter
    }

}