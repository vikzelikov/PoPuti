package bonch.dev.data.driver.signup

import androidx.preference.PreferenceManager
import bonch.dev.presentation.presenter.driver.signup.DriverSignupPresenter
import bonch.dev.utils.Constants

class SignupStatusModel(val driverSignupPresenter: DriverSignupPresenter) {

    fun getStatusSignup() {
        var status: Int? = getStatusLocal()
        //TODO

        if (status == null) {
            //мы не знаем статуса, поэтому спрашиваем сервер
            //retrofit

            //status = getStatusFromNet()
            //временно для теста
            status = Constants.DRIVER_SIGNUP_START


            if (status == Constants.DRIVER_SIGNUP_PROCESS) {
                //get data about checking docs
            }
            //setStatus(status)
            driverSignupPresenter.receiveStatus(status)

        } else {
            when (status) {
                Constants.DRIVER_SIGNUP_START -> {
                    status = Constants.DRIVER_SIGNUP_START
                }


                Constants.DRIVER_SIGNUP_PROCESS -> {
                    status = Constants.DRIVER_SIGNUP_PROCESS
                }


                Constants.DRIVER_SIGNUP_COMPLETE -> {
                    status = Constants.DRIVER_SIGNUP_COMPLETE
                }
            }

            driverSignupPresenter.receiveStatus(status)
        }

    }


    private fun setStatus(status: Int) {
        val activity = driverSignupPresenter.driverSignupActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        val editor = pref.edit()
        editor.putBoolean(status.toString(), true)
        editor.apply()
    }


    private fun getStatusLocal(): Int? {
        var status: Int? = null
        val activity = driverSignupPresenter.driverSignupActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)

        val isComplete = pref.getBoolean(Constants.DRIVER_SIGNUP_COMPLETE.toString(), false)
        val isProcess = pref.getBoolean(Constants.DRIVER_SIGNUP_PROCESS.toString(), false)
        val isStart = pref.getBoolean(Constants.DRIVER_SIGNUP_START.toString(), false)

        if (isStart) {
            status = Constants.DRIVER_SIGNUP_START
        }

        if (isProcess) {
            status = Constants.DRIVER_SIGNUP_PROCESS
        }

        if (isComplete) {
            status = Constants.DRIVER_SIGNUP_COMPLETE
        }

        return status
    }


    private fun isSignupProcess(): Boolean {
        val activity = driverSignupPresenter.driverSignupActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
        return pref.getBoolean(Constants.DRIVER_SIGNUP_PROCESS.toString(), false)
    }

}