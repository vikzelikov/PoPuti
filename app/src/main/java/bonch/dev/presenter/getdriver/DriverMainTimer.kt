package bonch.dev.presenter.getdriver

import android.R
import android.app.AlertDialog
import android.os.CountDownTimer
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.adapters.DriversListAdapter
import bonch.dev.utils.Constants.MAX_TIME_GET_DRIVER


object DriverMainTimer {

    private var testTimer: DriverTimer? = null

    fun getInstance(list: ArrayList<Driver>, adapter: DriversListAdapter): DriverTimer? {
        if (testTimer == null) {
            //interval 18 ~ 30 sec
            testTimer = DriverTimer(60000 * MAX_TIME_GET_DRIVER, 18, list, adapter)
        }

        return testTimer
    }


    class DriverTimer(startTime: Long, interval: Long, val list: ArrayList<Driver>, val adapter: DriversListAdapter) :
        CountDownTimer(startTime, interval) {

        override fun onFinish() {
            //TODO
            //show alert
            println("Пользователь не смог выбрать водителя для поездки")
            val context = adapter.getDriverPresenter.getDriverView.context
            AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    // Continue with delete operation
                } // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.no, null)
                .setIcon(R.drawable.ic_dialog_alert)
                .show()
        }

        override fun onTick(millisUntilFinished: Long) {
            try {
                for (i in 0 until list.size) {
                    list[i].timeLine?.let {
                        list[i].timeLine = it.dec()

                        //if timeLine too small, remove item
                        if(it < 50){
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
