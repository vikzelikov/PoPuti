package bonch.dev.presentation.modules.passanger.signup.view

import androidx.fragment.app.FragmentManager
import bonch.dev.presentation.interfaces.IBaseView

interface ContractView {

    interface IPhoneView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun showError(text: String)
        fun test(): FragmentManager?
    }


    interface IConfirmView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun getCode(): String
        fun requestFocus()
        fun showError()
        fun hideError()
        fun setViewTimer()
        fun finishTimer()
    }


    interface IFullNameView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun setHintListener()
        fun getFirstName(): String
        fun getLastName(): String
    }
}