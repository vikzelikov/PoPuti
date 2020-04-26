package bonch.dev.presentation.modules.passanger.signup.view

import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.presentation.interfaces.IBaseView

interface ContractView {

    interface IPhoneView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun showError(text: String)
        fun setPhoneMask()
    }


    interface IConfirmView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun getCode(): String
        fun showError()
        fun hideError()
        fun setViewTimer()
        fun finishTimer()
    }


    interface IFullNameView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun setHintListener()
        fun getProfileData(): Profile
        fun getFirstName(): String
        fun getLastName(): String
    }
}