package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.driver.getpassenger.SelectOrder
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.ride.orfferprice.view.OfferPriceView
import bonch.dev.presentation.modules.common.ride.routing.Routing
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import bonch.dev.presentation.modules.driver.getpassenger.view.ContractView
import com.yandex.mapkit.geometry.Point
import javax.inject.Inject

class DetailOrderPresenter : BasePresenter<ContractView.IDetailOrderView>(),
    ContractPresenter.IDetailOrderPresenter {

    @Inject
    lateinit var routing: Routing

    val OFFER_PRICE = 1


    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    override fun receiveOrder(order: RideInfo?) {
        if (order != null) {
            val map = getView()?.getMap()
            val fromLat = order.fromLat
            val fromLng = order.fromLng
            val toLat = order.toLat
            val toLng = order.toLng

            //check
            val fromPoint = if (fromLat != null && fromLng != null) {
                Point(fromLat, fromLng)
            } else {
                null
            }

            //check
            val toPoint = if (toLat != null && toLng != null) {
                Point(toLat, toLng)
            } else {
                null
            }

            //set directions
            if (fromPoint != null && toPoint != null && map != null) {
                //set routes
                routing.submitRequest(fromPoint, toPoint, true, map)
            }

            //set UI
            getView()?.setOrder(order)
        } else {
            val res = App.appComponent.getContext().resources
            getView()?.showNotification(res.getString(R.string.errorSystem))
        }
    }


    private fun getUserPoint(): Point? {
        val userLocation = getView()?.getUserLocationLayer()
        return userLocation?.cameraPosition()?.target
    }


    override fun offerPrice(context: Context, fragment: Fragment) {
        val intent = Intent(context, OfferPriceView::class.java)
        fragment.startActivityForResult(intent, OFFER_PRICE)
    }


    override fun onObjectUpdate() {
        //app accessed user geo
        val userPoint = getUserPoint()
        val map = getView()?.getMap()
        val fromLat = SelectOrder.order?.fromLat
        val fromLng = SelectOrder.order?.fromLng
        val fromPoint = if (fromLat != null && fromLng != null) {
            Point(fromLat, fromLng)
        } else {
            null
        }

        if (userPoint != null && fromPoint != null && map != null) {
            Routing()
                .submitRequest(userPoint, fromPoint, false, map)
        }
    }


    override fun showRoute() {
        routing.showRoute()
    }


    override fun instance(): DetailOrderPresenter {
        return this
    }

}