package bonch.dev.presentation.modules.driver.getpassenger.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.di.component.driver.DaggerGetPassengerComponent
import bonch.dev.di.module.driver.GetPassengerModule
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.adapters.OrdersAdapter
import bonch.dev.presentation.modules.driver.getpassenger.presenter.ContractPresenter
import bonch.dev.presentation.modules.driver.getpassenger.presenter.OrdersTimer
import kotlinx.android.synthetic.main.orders_view_fragment.*
import javax.inject.Inject

class OrdersView : Fragment(), ContractView.IOrdersView {

    @Inject
    lateinit var ordersPresenter: ContractPresenter.IOrdersPresenter

    @Inject
    lateinit var ordersAdapter: OrdersAdapter

    private val FEEDBACK = 1
    private val TIME_EXPIRED = 2

    init {
        initDI()

        GetPassengerComponent.getPassengerComponent?.inject(this)

        ordersPresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (GetPassengerComponent.getPassengerComponent == null) {
            GetPassengerComponent.getPassengerComponent = DaggerGetPassengerComponent
                .builder()
                .getPassengerModule(GetPassengerModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ActiveRide.activeRide?.let { ride ->
            //next step
            ordersPresenter.selectOrder(ride)
        }

        return inflater.inflate(R.layout.orders_view_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Permissions.access(Permissions.GEO_PERMISSION_REQUEST, this)

        ordersPresenter.startProcessBlock()

        initAdapter()

        setListeners()

        //observable on searching orders
        OrdersTimer.startTimer(ordersAdapter)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        context?.let {
            ordersPresenter.instance().isUserGeoAccess =
                if (Permissions.isAccess(Permissions.GEO_PERMISSION, it)) {
                    true
                } else {
                    Permissions.access(Permissions.GEO_PERMISSION_REQUEST, this)
                    false
                }
        }

        ordersPresenter.initOrders()

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == FEEDBACK) {
            (activity as? MainActivity)?.showNotification(getString(R.string.thanksForRate))
        }

        if (resultCode == TIME_EXPIRED) {
            (activity as? MainActivity)?.showNotification(getString(R.string.userCancelledRide))
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun setListeners() {}


    override fun showRecycler() {
        progress_bar.visibility = View.GONE
        text_search_orders.visibility = View.GONE
        orders_recycler.visibility = View.VISIBLE
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.getNavHost()
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let { Keyboard.hideKeyboard(it, orders_container) }
    }


    private fun initAdapter() {
        orders_recycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = ordersAdapter
        }
    }


    override fun getAdapter(): OrdersAdapter {
        return ordersAdapter
    }


    override fun getFragment(): Fragment {
        return this
    }


    override fun showOrdersLoading() {
        progress_bar?.visibility = View.VISIBLE
        text_search_orders?.visibility = View.VISIBLE
    }


    override fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


    override fun onDestroy() {
        ordersPresenter.instance().detachView()
        super.onDestroy()
    }
}