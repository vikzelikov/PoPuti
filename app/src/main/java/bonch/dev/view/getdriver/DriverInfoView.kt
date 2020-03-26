package bonch.dev.view.getdriver

import android.view.View
import android.widget.Toast
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.DriverInfoPresenter
import bonch.dev.utils.Constants
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

        driverInfoPresenter?.startTrackingDriver()
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
        var reasonID: Int = Constants.REASON4
        val cancelRideBtn = root.cancel_ride
        val cancelBtn = root.cancel
        val notCancelBtn = root.not_cancel
        val onMapView = root.on_map_view
        val callDriver = root.phone_call_driver
        val chatDriver = root.message_driver
        val case1 = root.case1
        val case2 = root.case2
        val case3 = root.case3
        val case4 = root.case4

        cancelRideBtn.setOnClickListener {
            driverInfoPresenter?.getCancelReason()
        }

        case1.setOnClickListener {
            reasonID = Constants.REASON1
            driverInfoPresenter?.getConfirmCancel()
        }

        case2.setOnClickListener {
            reasonID = Constants.REASON2
            driverInfoPresenter?.getConfirmCancel()
        }

        case3.setOnClickListener {
            reasonID = Constants.REASON3
            driverInfoPresenter?.getConfirmCancel()
        }

        case4.setOnClickListener {
            reasonID = Constants.REASON4
            driverInfoPresenter?.getConfirmCancel()
        }

        cancelBtn.setOnClickListener {
            driverInfoPresenter?.cancelDone(reasonID)
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

        if(driver.isArrived){
            getRootView().status_driver.text = getView().resources.getString(R.string.driverArrived)
            driverInfoPresenter?.isDriverArrived = true
        }else{
            getRootView().status_driver.text = getView().resources.getString(R.string.driverInWay)
            driverInfoPresenter?.isDriverArrived = false
        }

        Glide.with(root).load(driver.imgDriver)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(root.img_driver)
    }


    fun getView(): GetDriverView {
        return getDriverView
    }

    fun getRootView(): View {
        return getDriverView.getDriverPresenter?.root!!
    }

}