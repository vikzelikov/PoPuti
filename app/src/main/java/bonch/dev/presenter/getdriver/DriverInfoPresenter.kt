package bonch.dev.presenter.getdriver

import android.view.View
import bonch.dev.MainActivity
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.utils.Constants
import bonch.dev.utils.Coordinator
import bonch.dev.view.getdriver.DriverInfoView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.get_driver_fragment.view.*
import kotlinx.android.synthetic.main.get_driver_layout.view.*

class DriverInfoPresenter(private val driverInfoView: DriverInfoView) {

    fun onSlideCancelReason(slideOffset: Float, root: View) {
        val onMapView = root.on_map_view

        if (slideOffset > 0) {
            onMapView.alpha = slideOffset * 0.8f
        }
    }


    fun onSlideConfirmCancel(slideOffset: Float, root: View) {
        val onView = root.on_view_cancel_reason

        if (slideOffset > 0) {
            onView.visibility = View.VISIBLE
            onView.alpha = slideOffset * 0.5f
        }
    }


    fun onChangedStateCancelReason(newState: Int, root: View) {
        val onMapView = root.on_map_view
        val mainInfoLayout = root.main_info_layout

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView.visibility = View.GONE
            mainInfoLayout.elevation = 30f
        } else {
            driverInfoView.confirmCancelBottomSheetBehavior!!.state =
                BottomSheetBehavior.STATE_COLLAPSED
            onMapView.visibility = View.VISIBLE
            mainInfoLayout.elevation = 0f
        }
    }


    fun onChangedStateConfirmCancel(newState: Int, root: View) {
        val onMapView = root.on_view_cancel_reason

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView.visibility = View.GONE
        } else {
            onMapView.visibility = View.VISIBLE
        }
    }


    fun getCancelReason() {
        driverInfoView.cancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun getConfirmCancel() {
        driverInfoView.confirmCancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun cancelDone() {
        val activity = driverInfoView.getView().activity
        val fm = (activity as MainActivity).supportFragmentManager
        Coordinator.replaceFragment(Constants.MAIN_FRAGMENT, null, fm)
    }


    fun notCancel() {
        val root = driverInfoView.getView().view
        root?.on_map_view?.visibility = View.GONE
        root?.on_view_cancel_reason?.visibility = View.GONE

        driverInfoView.cancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        driverInfoView.confirmCancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun setInfoDriver(driver: Driver) {
        driverInfoView.setInfoDriver(driver)
    }

}