package bonch.dev.presentation.modules.passanger.signup.presenter

import android.view.View
import androidx.fragment.app.FragmentActivity
import bonch.dev.MainActivity

interface ContractPresenter {

    interface IPhonePresenter : ContractPresenter {
        fun instance(): PhonePresenter
        fun getCode(phone: String, root: View?)
        fun isPhoneEntered(phone: String): Boolean
        fun hideKeyboard(activity: FragmentActivity)
    }


    interface IConfirmPhonePresenter : ContractPresenter {
        fun instance(): ConfirmPhonePresenter
        fun startTimerRetrySend(activity: MainActivity)
        fun isCodeEnter(): Boolean
        fun checkCode(phone: String?, code: String)
        fun setViewTimer()
        fun timerFinish()
        fun onResponseCheckCode(isCorrect: Boolean)
        fun back(activity: FragmentActivity)
    }


    interface IFullNamePresenter : ContractPresenter {
        fun instance(): FullNamePresenter
        fun isNameEntered(): Boolean
        fun saveProfileData(firstName: String, lastName: String)
        fun back(activity: FragmentActivity)
    }
}