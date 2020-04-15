package bonch.dev.view.passanger.getdriver

import android.view.View
import android.widget.Toast
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.Driver
import bonch.dev.presenter.passanger.getdriver.DriverInfoPresenter
import bonch.dev.utils.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.driver_info_layout.*
import kotlinx.android.synthetic.main.driver_info_layout.view.*
import kotlinx.android.synthetic.main.get_driver_layout.view.*

class DriverInfoView(private var getDriverView: GetDriverView) {

    private var driverInfoPresenter: DriverInfoPresenter? = null
    var cancelBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var confirmCancelBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var driverCancelledBottomSheet: BottomSheetBehavior<*>? = null


    init {
        if (driverInfoPresenter == null) {
            driverInfoPresenter = DriverInfoPresenter(this)
        }
    }


    fun onCreateView(driver: Driver) {
        setListeners()

        setBottomSheet()

        driverInfoPresenter?.setInfoDriver(driver)

        driverInfoPresenter?.startTrackingDriver()

        //set map correct relative other views
        driverInfoPresenter?.correctMapView()
    }


    private fun setBottomSheet() {
        cancelBottomSheetBehavior = BottomSheetBehavior.from<View>(getView().reasons_bottom_sheet)
        confirmCancelBottomSheetBehavior =
            BottomSheetBehavior.from<View>(getView().confirm_cancel_bottom_sheet)
        driverCancelledBottomSheet =
            BottomSheetBehavior.from<View>(getView().driver_cancelled_bottom_sheet)

        cancelBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                driverInfoPresenter?.onSlideCancelReason(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                driverInfoPresenter?.onChangedStateCancelReason(newState)
            }
        })


        confirmCancelBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                driverInfoPresenter?.onSlideConfirmCancel(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                driverInfoPresenter?.onChangedStateConfirmCancel(newState)
            }
        })


        driverCancelledBottomSheet!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                driverInfoPresenter?.onSlideCancelReason(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                driverInfoPresenter?.onChangedStateCancelReason(newState)
            }
        })
    }


    private fun setListeners() {
        val r = getView()
        //set default reason
        var reasonID: Int = Constants.REASON4


        r.cancel_ride.setOnClickListener {
            driverInfoPresenter?.getCancelReason()
        }

        r.case1.setOnClickListener {
            reasonID = Constants.REASON1
            driverInfoPresenter?.getConfirmCancel()
        }

        r.case2.setOnClickListener {
            reasonID = Constants.REASON2
            driverInfoPresenter?.getConfirmCancel()
        }

        r.case3.setOnClickListener {
            reasonID = Constants.REASON3
            driverInfoPresenter?.getConfirmCancel()
        }

        r.case4.setOnClickListener {
            reasonID = Constants.REASON4
            driverInfoPresenter?.getConfirmCancel()
        }

        r.cancel.setOnClickListener {
            driverInfoPresenter?.cancelDone(reasonID)
        }

        r.not_cancel.setOnClickListener {
            driverInfoPresenter?.hideAllBottomSheet()
        }

        r.on_map_view.setOnClickListener {
            driverInfoPresenter?.hideAllBottomSheet()
        }

        //TODO
        r.message_driver.setOnClickListener {
            Toast.makeText(getView().context!!, "Chat", Toast.LENGTH_SHORT).show()
        }

        r.phone_call_driver.setOnClickListener {
            Toast.makeText(getView().context!!, "Call driver", Toast.LENGTH_SHORT).show()
        }
    }


    fun setInfoDriver(driver: Driver) {
        val r = getView()

        r.driver_name.text = driver.nameDriver
        r.car_number.text = driver.carNumber
        r.car_name.text = driver.carName

        if (driver.isArrived) {
            r.status_driver.text = getView().resources.getString(R.string.driverArrived)
            driverInfoPresenter?.isDriverArrived = true
        } else {
            r.status_driver.text = getView().resources.getString(R.string.driverInWay)
            driverInfoPresenter?.isDriverArrived = false
        }

        Glide.with(r).load(driver.imgDriver)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(r.img_driver)
    }


    fun onBackPressed(): Boolean {
        return driverInfoPresenter?.onBackPressed()!!
    }


    fun getView(): GetDriverView {
        return getDriverView
    }

}