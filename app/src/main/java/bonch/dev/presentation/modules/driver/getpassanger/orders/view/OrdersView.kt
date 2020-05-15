package bonch.dev.presentation.modules.driver.getpassanger.orders.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.App
import bonch.dev.R
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.driver.getpassanger.orders.presenter.OrdersPresenter
import javax.inject.Inject

class OrdersView : Fragment(), IBaseView {

    @Inject
    lateinit var ordersPresenter: OrdersPresenter

    init {
        //App.getPassangerComponent?.inject(this)

//        ordersPresenter.attachView(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.orders_view_fragment, container, false)

        //ordersPresenter.showBottomSheet()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun setListeners() {

    }

    override fun getNavHost(): NavController {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideKeyboard() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onDestroy() {
        ordersPresenter.detachView()
        super.onDestroy()
    }
}