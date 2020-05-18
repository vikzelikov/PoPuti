package bonch.dev.presentation.modules.driver.getpassanger.presenter

import android.content.Intent
import android.os.Handler
import bonch.dev.R
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.entities.driver.getpassanger.SelectOrder
import bonch.dev.domain.interactor.driver.getpassanger.IGetPassangerInteractor
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import bonch.dev.presentation.modules.driver.getpassanger.view.ContractView
import bonch.dev.presentation.modules.driver.getpassanger.view.MapOrderView
import bonch.dev.route.MainRouter
import javax.inject.Inject


class OrdersPresenter : BasePresenter<ContractView.IOrdersView>(),
    ContractPresenter.IOrdersPresenter {

    @Inject
    lateinit var getPassangerInteractor: IGetPassangerInteractor

    private var blockHandler: Handler? = null
    private var mainHandler: Handler? = null
    private var isBlock = false

    var isUserGeoAccess = false

    init {
        GetPassangerComponent.getPassangerComponent?.inject(this)
    }


    override fun onClickItem(order: Order) {
        if (!isBlock) {
            getView()?.getFragment()?.context?.let {
                SelectOrder.order = order

                //show detail order
                val intent = Intent(it, MapOrderView::class.java)
                getView()?.getFragment()?.startActivityForResult(intent, 1)
            }

            isBlock = true
        }
    }


    override fun startSearchOrders() {
        //TODO replace to searching with server
        mainHandler = Handler()
        mainHandler?.postDelayed(object : Runnable {
            override fun run() {
                getPassangerInteractor.getNewOrder {
                    getView()?.showRecycler()
                    getView()?.getAdapter()?.setNewOrder(it)
                }
                mainHandler?.postDelayed(this, 5000)
            }
        }, 3000)
    }


    override fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 3500)
            }
        }, 0)
    }


    override fun instance(): OrdersPresenter {
        return this
    }

}