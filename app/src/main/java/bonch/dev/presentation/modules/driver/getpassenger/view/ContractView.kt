package bonch.dev.presentation.modules.driver.getpassenger.view

import androidx.fragment.app.Fragment
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.driver.getpassenger.adapters.OrdersAdapter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractView {

    interface IOrdersView : IBaseView {
        fun getAdapter(): OrdersAdapter
        fun getFragment(): Fragment
        fun showOrdersLoading()
        fun showRecycler()
    }


    interface IMapOrderView : IBaseView {
        fun moveCamera(point: Point)
        fun getMap(): MapView
        fun finishMapActivity()
        fun finishMapActivity(resultCode: Int)
        fun getUserLocation(): UserLocationLayer?
    }


    interface IDetailOrderView : IBaseView {
        fun nextFragment()
        fun getMap(): MapView?
        fun setOrder(order: RideInfo)
        fun hideOfferPrice(isShowNotification: Boolean)
        fun getUserLocationLayer(): UserLocationLayer?
        fun onBackPressed(): Boolean
        fun passengerCancelRide()
        fun onObjectUpdate()
    }

    interface ITrackRideView : IBaseView {
        fun setOrder(order: RideInfo)
        fun passengerCancelRide(payment: Int)
        fun tickTimerWaitPassanger(sec: Int, isPaidWaiting: Boolean)
        fun onBackPressed(): Boolean
        fun stepWaitPassanger()
        fun getMap(): MapView?
        fun stepDoneRide()
        fun stepInWay()
        fun finish()
    }

}