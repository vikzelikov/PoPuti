package bonch.dev.poputi.presentation.modules.passenger.signup.view

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface ContractView {

    interface IPhoneView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun setPhoneMask()
    }


    interface IConfirmView : IBaseView {
        fun changeBtnEnable(enable: Boolean)
        fun getCode(): String?
        fun showError()
        fun hideError()
        fun setViewTimer()
        fun finishTimer()
        fun setSoftInput()
    }


    interface IFullNameView : IBaseView {
        fun setHintListener()
        fun changeBtnEnable(enable: Boolean)
        fun getProfileData(): Profile
        fun getFirstName(): String?
        fun getLastName(): String?
    }
}