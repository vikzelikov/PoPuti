package bonch.dev.presentation.modules.passanger.getdriver.ride.presenter

import android.os.CountDownTimer
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.DriversListAdapter


object DriverMainTimer {

    private const val MAX_TIME_GET_DRIVER = 3L //min
    const val TIME_EXPIRED_ITEM = 30.0 //sec
    var DEFAULT_WIDTH: Int = 0
    //ratio for smoothing animation (more ratio -> more smooth anim)
    var ratio = 100

    private var driverTimer: DriverTimer? = null

    fun getInstance(adapter: DriversListAdapter): DriverTimer? {
        if (driverTimer == null) {
            //interval 18 ~ 30 sec
            driverTimer =
                DriverTimer(
                    60000 * MAX_TIME_GET_DRIVER,
                    100,
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
                        list[i].timeLine -= 0.1

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
