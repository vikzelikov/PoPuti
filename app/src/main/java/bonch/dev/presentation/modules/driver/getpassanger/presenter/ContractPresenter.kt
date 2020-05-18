package bonch.dev.presentation.modules.driver.getpassanger.presenter

import android.content.Context
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.domain.entities.driver.getpassanger.Order
import bonch.dev.domain.entities.driver.getpassanger.ReasonCancel

interface ContractPresenter {

    interface IOrdersPresenter {
        fun onClickItem(order: Order)
        fun startSearchOrders()
        fun startProcessBlock()
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
        fun instance(): DetailOrderPresenter
        fun receiveOrder(order: Order?)
        fun offerPrice(context: Context, fragment: Fragment)
        fun onObjectUpdate()
        fun showRoute()
    }


    interface ITrackRidePresenter {
        fun instance(): TrackRidePresenter
        fun receiveOrder(order: Order?)
        fun showChat(context: Context, fragment: Fragment)
        fun cancelDoneOtherReason(comment: String)
        fun cancelDone(reasonID: ReasonCancel)
    }

}