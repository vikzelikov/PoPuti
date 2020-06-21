package bonch.dev.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.driver.getpassenger.ReasonCancel
import bonch.dev.presentation.interfaces.ParentHandler
import com.yandex.mapkit.geometry.Point

interface ContractPresenter {

    interface IOrdersPresenter {
        fun initOrders()
        fun startSearchOrders()
        fun startProcessBlock()
        fun onClickItem(order: RideInfo)
        fun calcDistance(from: Point, to: Point): Int
        fun instance(): OrdersPresenter
    }


    interface IMapOrderPresenter {
        fun getBitmap(drawableId: Int): Bitmap?
        fun attachDetailOrder(fm: FragmentManager)
        fun attachTrackRide(fm: FragmentManager)
        fun attachRateRide(fm: FragmentManager)
        fun onBackPressed(): Boolean
        fun onObjectUpdate()
        fun instance(): MapOrderPresenter
    }


    interface IDetailOrderPresenter {
        fun subscribeOnChangeRide()
        fun instance(): DetailOrderPresenter
        fun receiveOrder(order: RideInfo?)
        fun offerPrice(context: Context, fragment: Fragment)
        fun offerPriceDone(price: Int)
        fun onObjectUpdate()
        fun nextFragment()
        fun onDestroy()
        fun showRoute()
    }


    interface ITrackRidePresenter {
        fun instance(): TrackRidePresenter
        fun receiveOrder(order: RideInfo?)
        fun showChat(context: Context, fragment: Fragment)
        fun tickTimerWaitPassanger(sec: Int, isPaidWating: Boolean)
        fun cancelDoneOtherReason(comment: String)
        fun cancelDone(reasonID: ReasonCancel)
        fun subscribeOnChangeRide()
        fun onDestroy()
        fun nextStep()
    }


    interface UserLocationListener : android.location.LocationListener {
        override fun onLocationChanged(location: Location?)
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}
    }

}