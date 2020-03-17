package bonch.dev.presenter.getdriver

import android.os.Bundle
import android.view.View
import bonch.dev.MainActivity
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.model.getdriver.pojo.RideDetailInfo
import bonch.dev.utils.Constants.FROM
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.RIDE_DETAIL_INFO
import bonch.dev.utils.Constants.TO
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.view.getdriver.GetDriverView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.get_driver_layout.view.*

class GetDriverPresenter(val getDriverView: GetDriverView) {

    fun receiveUserData(bundle: Bundle?, root: View) {
        bundle?.let {
            val from: Ride = it.getParcelable(FROM)!!
            val to: Ride = it.getParcelable(TO)!!
            val rideDetailInfo: RideDetailInfo? = it.getParcelable(RIDE_DETAIL_INFO)

            root.from_address.text = from.address!!
            root.to_address.text = to.address!!

            if (rideDetailInfo?.priceRide != null) {
                root.offer_price.text = rideDetailInfo.priceRide
            }

            if (rideDetailInfo?.comment != null) {
                root.comment_min_text.text = rideDetailInfo.comment
            }
        }
    }


    fun onSlideBottomSheet(slideOffset: Float, root: View) {
        val onMapView = root.on_map_view

        if (slideOffset > 0) {
            onMapView.alpha = slideOffset * 0.8f
        }
    }


    fun onStateChangedBottomSheet(newState: Int, root: View) {
        val onMapView = root.on_map_view
        val mainInfoLayout = root.main_info_layout


        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView.visibility = View.GONE
            mainInfoLayout.elevation = 30f
        } else {
            onMapView.visibility = View.VISIBLE
            mainInfoLayout.elevation = 0f
        }
    }


    fun getCancelReason() {
        getDriverView.cancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun cancelDone() {
        val fm = (getDriverView.activity as MainActivity).supportFragmentManager
        replaceFragment(MAIN_FRAGMENT, null, fm)
    }

}