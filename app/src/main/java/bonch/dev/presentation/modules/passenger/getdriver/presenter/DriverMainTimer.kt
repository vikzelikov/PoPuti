package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.os.CountDownTimer
import android.util.Log
import bonch.dev.presentation.modules.passenger.getdriver.adapters.DriversListAdapter


object DriverMainTimer {

    private const val MAX_TIME_GET_DRIVER = 3L //min
    const val TIME_EXPIRED_ITEM = 30.0 //sec
    var DEFAULT_WIDTH: Int = 0
    //ratio for smoothing animation (more ratio -> more smooth anim)
    var ratio = 100

    private var driverTimer: DriverTimer? = null

    fun getInstance(adapter: DriversListAdapter): DriverTimer? {
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
        val adapter: DriversListAdapter
    ) : CountDownTimer(startTime, interval) {

        override fun onFinish() {
            adapter.getDriverPresenter.instance().getView()?.getExpiredTimeConfirm()
        }

        override fun onTick(millisUntilFinished: Long) {
            try {
                val list = adapter.list

                for (i in 0 until list.size) {
                    list[i].timeLine.let {
                        list[i].timeLine -= 1

                        //if timeLine too small, remove item
                        if (it < 2) {
                            adapter.rejectDriver(null, false)
                        }
                    }
                }
            } catch (ex: IndexOutOfBoundsException) {
                println(ex.message)
            }
        }
    }
}
