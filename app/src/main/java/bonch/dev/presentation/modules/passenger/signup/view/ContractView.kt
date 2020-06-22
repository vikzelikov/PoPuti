package bonch.dev.presentation.modules.passenger.signup.view

import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.presentation.interfaces.IBaseView

interface ContractView {

    interface IPhoneView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
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
        fun setHintListener()
        fun changeBtnEnable(enable: Boolean)
        fun getProfileData(): Profile
        fun getFirstName(): String
        fun getLastName(): String
    }
}