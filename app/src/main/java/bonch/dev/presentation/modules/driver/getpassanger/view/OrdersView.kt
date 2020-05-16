package bonch.dev.presentation.modules.driver.getpassanger.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.data.driver.getpassanger.pojo.Order
import bonch.dev.di.component.driver.DaggerGetPassangerComponent
import bonch.dev.di.module.driver.GetPassangerModule
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import bonch.dev.presentation.modules.driver.getpassanger.adapters.OrdersAdapter
import bonch.dev.presentation.modules.driver.getpassanger.presenter.ContractPresenter
import kotlinx.android.synthetic.main.orders_view_fragment.*
import javax.inject.Inject

class OrdersView : Fragment(), ContractView.IOrdersView {

    @Inject
    lateinit var ordersPresenter: ContractPresenter.IOrdersPresenter

    @Inject
    lateinit var ordersAdapter: OrdersAdapter


    init {
        initDI()

        GetPassangerComponent.getPassangerComponent?.inject(this)

        ordersPresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (GetPassangerComponent.getPassangerComponent == null) {
            GetPassangerComponent.getPassangerComponent = DaggerGetPassangerComponent
                .builder()
                .getPassangerModule(GetPassangerModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.orders_view_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        setListeners()
    }


    override fun setListeners() {

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


        val arr = arrayListOf<Order>()
        arr.add(Order(3333))
        arr.add(Order(123))


        ordersAdapter.list = arr
        ordersAdapter.notifyDataSetChanged()

        Handler().postDelayed({
            val order = Order(96)
            ordersAdapter.list.add(order)
            ordersAdapter.notifyItemInserted(2)
        }, 5000)

    }


    override fun onDestroy() {
        ordersPresenter.instance().detachView()
        super.onDestroy()
    }
}