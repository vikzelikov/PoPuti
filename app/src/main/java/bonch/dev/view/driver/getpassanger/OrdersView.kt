package bonch.dev.view.driver.getpassanger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.ContractInterface
import bonch.dev.presenter.driver.getpassanger.OrdersPresenter
import javax.inject.Inject

class OrdersView : Fragment(), ContractInterface.IView {

    @Inject
    lateinit var ordersPresenter: OrdersPresenter

    init {
        App.mGetPassangerComponent?.inject(this)

        ordersPresenter.attachView(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.orders_view_fragment, container, false)

        ordersPresenter.showBottomSheet()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun setListeners() {

    }


    override fun onDestroy() {
        ordersPresenter.detachView()
        super.onDestroy()
    }
}