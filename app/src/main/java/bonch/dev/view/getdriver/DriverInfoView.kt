package bonch.dev.view.getdriver

import android.view.View
import android.widget.Toast
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.DriverInfoPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.driver_info_layout.view.*

class DriverInfoView(var getDriverView: GetDriverView) {

    private var driverInfoPresenter: DriverInfoPresenter? = null
    var cancelBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var confirmCancelBottomSheetBehavior: BottomSheetBehavior<*>? = null


    init {
        if (driverInfoPresenter == null) {
            driverInfoPresenter = DriverInfoPresenter(this)
        }
    }


    fun onCreateView(driver: Driver) {
        setBottomSheet(getRootView())

        setListeners(getRootView())

        driverInfoPresenter?.setInfoDriver(driver)
    }


    private fun setBottomSheet(root: View) {
        cancelBottomSheetBehavior = BottomSheetBehavior.from<View>(root.reasons_bottom_sheet)
        confirmCancelBottomSheetBehavior = BottomSheetBehavior.from<View>(root.confirm_cancel_bottom_sheet)

        cancelBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                driverInfoPresenter?.onSlideCancelReason(slideOffset, root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                driverInfoPresenter?.onChangedStateCancelReason(newState, root)
            }
        })


        confirmCancelBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                driverInfoPresenter?.onSlideConfirmCancel(slideOffset, root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                driverInfoPresenter?.onChangedStateConfirmCancel(newState, root)
            }
        })
    }


    private fun setListeners(root: View) {
        val cancelRideBtn = root.cancel_ride
        val cancelBtn = root.cancel
        val notCancelBtn = root.not_cancel
        val onMapView = root.on_map_view
        val callDriver = root.phone_call_driver
        val chatDriver = root.message_driver
        val case1 = root.case1

        cancelRideBtn.setOnClickListener {
            driverInfoPresenter?.getCancelReason()
        }

        //TODO
        case1.setOnClickListener {
            driverInfoPresenter?.getConfirmCancel()
        }

        cancelBtn.setOnClickListener {
            driverInfoPresenter?.cancelDone()
        }

        notCancelBtn.setOnClickListener {
            driverInfoPresenter?.notCancel()
        }

        onMapView.setOnClickListener {
            driverInfoPresenter?.notCancel()
        }

        //TODO
        chatDriver.setOnClickListener {
            Toast.makeText(getView().context!!, "Chat", Toast.LENGTH_SHORT).show()
        }

        callDriver.setOnClickListener {
            Toast.makeText(getView().context!!, "Call driver", Toast.LENGTH_SHORT).show()
        }
    }


    fun setInfoDriver(driver: Driver) {
        val root = getRootView()

        root.driver_name.text = driver.nameDriver
        root.car_number.text = driver.carNumber
        root.car_name.text = driver.carName

        Glide.with(root).load(driver.imgDriver)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(root.img_driver)
    }


    fun getView(): GetDriverView {
        return getDriverView
    }

    private fun getRootView(): View {
        return getDriverView.view!!
    }


}