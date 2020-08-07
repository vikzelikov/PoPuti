package bonch.dev.poputi.presentation.modules.driver.getpassenger.presenter

import android.content.Context
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.entities.driver.getpassenger.ReasonCancel
import com.yandex.mapkit.geometry.Point

interface ContractPresenter {

    interface IOrdersPresenter {
        fun initOrders()
        fun startSearchOrders()
        fun startProcessBlock()
        fun selectOrder(order: RideInfo)
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
        fun subscribeOnRide()
        fun instance(): DetailOrderPresenter
        fun receiveOrder(order: RideInfo?)
        fun offerPrice(context: Context, fragment: Fragment)
        fun cancelOffer(byDriver: Boolean)
        fun offerPriceDone(price: Int)
        fun startProcessBlock()
        fun onObjectUpdate()
        fun nextFragment()
        fun onDestroy()
        fun showRoute()
    }


    interface ITrackRidePresenter {
        fun registerReceivers()
        fun getByValue(status: Int?): StatusRide?
        fun instance(): TrackRidePresenter
        fun receiveOrder(order: RideInfo?)
        fun showChat(context: Context, fragment: Fragment)
        fun tickTimerWaitPassanger(sec: Long, isPaidWating: Boolean)
        fun cancelDone(reasonID: ReasonCancel, textReason: String)
        fun cancelDoneOtherReason(comment: String)
        fun changeState(step: StatusRide, isRestoreRide: Boolean)
        fun stopService()
        fun clearRide()
        fun nextStep()
    }

}