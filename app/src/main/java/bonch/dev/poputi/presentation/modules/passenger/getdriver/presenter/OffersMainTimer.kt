package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.os.CountDownTimer
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.OffersAdapter


object OffersMainTimer {

    private const val MAX_TIME_GET_DRIVER = 3L //min
    const val TIME_EXPIRED_ITEM = 30L //sec
    var DEFAULT_WIDTH: Int = 0
    //ratio for smoothing animation (more ratio -> more smooth anim)
    var ratio = 100

    private var driverTimer: DriverTimer? = null

    fun getInstance(adapter: OffersAdapter): DriverTimer? {
        if (driverTimer == null) {
            driverTimer =
                DriverTimer(
                    60000 * MAX_TIME_GET_DRIVER,
                    1000,
                    adapter
                )
        }

        return driverTimer
    }


    fun getInstance(): DriverTimer? {
        return driverTimer
    }


    fun deleteInstance() {
        driverTimer = null
    }


    class DriverTimer(
        startTime: Long,
        interval: Long,
        val adapter: OffersAdapter
    ) : CountDownTimer(startTime, interval) {

        override fun onFinish() {
            adapter.getOffersPresenter.instance().getView()?.getExpiredTimeConfirm()

            adapter.getOffersPresenter.timeExpired(
                App.appComponent.getContext().getString(
                    R.string.mistake_order
                )
            )
        }

        override fun onTick(millisUntilFinished: Long) {
            try {
                val list = adapter.list

                for (i in 0 until list.size) {
                    list[i].timeLine.let {
                        list[i].timeLine -= 1
                        //if timeLine too small, remove item
                        if (it < 2) {
                            adapter.rejectOffer(null, false)
                        }
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }
}
