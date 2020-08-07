package bonch.dev.poputi.presentation.modules.passenger.signup.presenter

import androidx.fragment.app.FragmentActivity
import bonch.dev.poputi.MainActivity

interface ContractPresenter {

    interface IPhonePresenter {
        fun instance(): PhonePresenter
        fun getCode(phone: String)
        fun isPhoneEntered(phone: String): Boolean
        fun hideKeyboard(activity: FragmentActivity)
    }


    interface IConfirmPhonePresenter {
        fun instance(): ConfirmPhonePresenter
        fun startTimerRetrySend(activity: MainActivity)
        fun isCodeEnter(): Boolean
        fun checkCode(phone: String?, code: String)
        fun setViewTimer()
        fun timerFinish()
        fun onResponseCheckCode(isCorrect: Boolean)
        fun back(activity: FragmentActivity)
    }


    interface IFullNamePresenter {
        fun instance(): FullNamePresenter
        fun isNameEntered(): Boolean
        fun saveToken()
        fun doneSignup()
        fun back(activity: FragmentActivity)
    }
}