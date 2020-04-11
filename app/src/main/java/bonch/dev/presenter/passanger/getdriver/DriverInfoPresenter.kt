package bonch.dev.presenter.passanger.getdriver

import android.os.Build
import android.text.Html
import android.view.View
import androidx.preference.PreferenceManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.Coordinate
import bonch.dev.model.passanger.getdriver.pojo.Driver
import bonch.dev.model.passanger.getdriver.pojo.DriverObject
import bonch.dev.model.passanger.getdriver.pojo.ReasonCancel
import bonch.dev.utils.Constants
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.view.passanger.getdriver.DriverInfoView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.driver_info_layout.view.*
import kotlinx.android.synthetic.main.get_driver_layout.view.main_info_layout
import kotlinx.android.synthetic.main.get_driver_layout.view.on_map_view
import kotlinx.android.synthetic.main.get_driver_layout.view.on_view_cancel_reason

class DriverInfoPresenter(private val driverInfoView: DriverInfoView) {


    var isDriverArrived = false


    fun startTrackingDriver() {
        //TODO
        //update icon driver on map
        //send request to server every 5 seconds
    }


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
        val textMessage = driverInfoView.getRootView().text_message

        if (isDriverArrived) {
            val resources = driverInfoView.getRootView().resources
            val tax = getTaxMoney()
            val message: String =
                driverInfoView.getView().resources.getString(R.string.messageWarningTakeMoney)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                message.plus(
                    Html.fromHtml(
                        resources.getString(R.string.offer_price_average_price)
                            .plus(" <b>$tax рублей</b>"), Html.FROM_HTML_MODE_COMPACT
                    )
                )

            } else {
                message.plus(
                    Html.fromHtml(
                        resources.getString(R.string.offer_price_average_price)
                            .plus(" <b>$tax рублей</b>")
                    )
                )
            }

            textMessage.text = message


        } else {
            textMessage.text =
                driverInfoView.getView().resources.getString(R.string.messageWarningDriverIs)
        }

        driverInfoView.confirmCancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun getTaxMoney(): Int {
        return 100
    }


    fun cancelDone(reasonID: Int) {
        //TODO send reason to server
        //stop getting new driver
        val fm = (driverInfoView.getView().activity as MainActivity).supportFragmentManager

        when (reasonID) {
            Constants.REASON1 -> {
                //Водитель попросил отменить
                ReasonCancel.reasonID = Constants.REASON1
            }

            Constants.REASON2 -> {
                //Долго ждать
                ReasonCancel.reasonID = Constants.REASON2
            }

            Constants.REASON3 -> {
                //по ошибке
                Coordinate.toAdr = null
                ReasonCancel.reasonID = Constants.REASON3
            }

            Constants.REASON4 -> {
                //другая причина
                Coordinate.toAdr = null
                ReasonCancel.reasonID = Constants.REASON4
            }
        }

        if (isDriverArrived) {
            //TODO
            //вычесть бабки
        }

        //clear data
        DriverObject.driver = null
        removeDataDriver()

        //redirect
        replaceFragment(Constants.MAIN_FRAGMENT, null, fm)
    }


    fun notCancel() {
        val root = driverInfoView.getView().view
        root?.on_map_view?.visibility = View.GONE
        root?.on_view_cancel_reason?.visibility = View.GONE

        driverInfoView.cancelBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        driverInfoView.confirmCancelBottomSheetBehavior!!.state =
            BottomSheetBehavior.STATE_COLLAPSED
    }


    fun setInfoDriver(driver: Driver) {
        driverInfoView.setInfoDriver(driver)
        //save driver in case close app

        if (driver.carName != null) {
            saveDataDriver(driver)
        }
    }


    private fun saveDataDriver(driver: Driver) {
        val activity = driverInfoView.getDriverView.activity as MainActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        editor.putString(Constants.NAME_DRIVER, driver.nameDriver)
        editor.putString(Constants.CAR_NAME, driver.carName)
        editor.putString(Constants.CAR_NUMBER, driver.carNumber)
        editor.putInt(Constants.PRICE_DRIVER, driver.price!!)
        editor.putInt(Constants.IMG_DRIVER, driver.imgDriver!!)
        editor.putBoolean(Constants.IS_DRIVER_ARRIVED, isDriverArrived)
        editor.apply()
    }


    private fun removeDataDriver() {
        val activity = driverInfoView.getDriverView.activity as MainActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)

        val editor = pref.edit()
        editor.remove(Constants.NAME_DRIVER)
        editor.remove(Constants.CAR_NAME)
        editor.remove(Constants.CAR_NUMBER)
        editor.remove(Constants.PRICE_DRIVER)
        editor.remove(Constants.IMG_DRIVER)
        editor.remove(Constants.IS_DRIVER_ARRIVED)
        editor.apply()
    }
}