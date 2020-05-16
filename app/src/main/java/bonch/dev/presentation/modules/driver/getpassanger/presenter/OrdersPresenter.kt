package bonch.dev.presentation.modules.driver.getpassanger.presenter

import android.os.Handler
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.interactor.driver.getpassanger.IGetPassangerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import bonch.dev.presentation.modules.driver.getpassanger.view.ContractView
import javax.inject.Inject


class OrdersPresenter : BasePresenter<ContractView.IOrdersView>(),
    ContractPresenter.IOrdersPresenter {

    @Inject
    lateinit var getPassangerInteractor: IGetPassangerInteractor

    private var mainHandler: Handler? = null

    init {
        GetPassangerComponent.getPassangerComponent?.inject(this)
    }


    override fun onClickItem(order: Order) {
        //show detail orders
    }


    override fun startSearchOrders() {
        //TODO replace to searching with server
        mainHandler = Handler()
        mainHandler?.postDelayed(object : Runnable {
            override fun run() {
                getPassangerInteractor.getNewOrder {
                    getView()?.getAdapter()?.setNewOrder(it)
                }
                mainHandler?.postDelayed(this, 5000)
            }
        }, 1000)
    }


    override fun instance(): OrdersPresenter {
        return this
    }

}