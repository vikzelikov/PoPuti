package bonch.dev.presentation.modules.driver.getpassanger.view

import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.driver.getpassanger.adapters.OrdersAdapter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractView {

    interface IOrdersView : IBaseView {
        fun getAdapter(): OrdersAdapter
    }


    interface IMapOrderView : IBaseView {
        fun moveCamera(point: Point)
        fun getMap(): MapView
        fun finishActivity()
        fun getUserLocation(): UserLocationLayer?
    }


    interface IDetailOrderView : IBaseView {
        fun setOrder(order: Order)
        fun getMap(): MapView?
        fun onObjectUpdate()
        fun getUserLocationLayer(): UserLocationLayer?
        fun onBackPressed(): Boolean
        fun showNotification(text: String)
    }

    interface ITrackRideView : IBaseView {
        fun setOrder(order: Order)
        fun getPassangerCancelled()
        fun showNotification(text: String)
        fun tickTimerWaitPassanger(sec: Int, isPaidWaiting: Boolean)
        fun stepWaitPassanger()
        fun getMap(): MapView?
        fun stepDoneRide()
        fun stepInWay()
        fun finish()
    }

}