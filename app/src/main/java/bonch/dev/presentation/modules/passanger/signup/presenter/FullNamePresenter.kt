package bonch.dev.presentation.modules.passanger.signup.presenter

import androidx.fragment.app.FragmentActivity
import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.domain.entities.passanger.signup.Phone
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.signup.view.ContractView

class FullNamePresenter : BasePresenter<ContractView.IFullNameView>(),
    ContractPresenter.IFullNamePresenter {

    override fun isNameEntered(): Boolean {
        var result = false

        if(getView() != null){
            val firstName = getView()!!.getFirstName()
            val lastName = getView()!!.getLastName()

            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                getView()?.changeBtnEnable(true)
                result = true
            } else {
                getView()?.changeBtnEnable(false)
            }
        }

        return result
    }


    override fun saveProfileData(firstName: String, lastName: String) {
        val profileData = Profile()

        if (firstName.trim().isNotEmpty() && lastName.trim().isNotEmpty()) {
            profileData.firstName = firstName.trim()
            profileData.lastName = lastName.trim()
            profileData.fullName = firstName.trim().plus(" ").plus(lastName.trim())
        }

        if (Phone.phone?.trim()!!.isNotEmpty()) {
            profileData.phone = Phone.phone?.trim()
        }

        //signupModel?.saveFullName(profileData)
    }


    override fun back(activity: FragmentActivity) {
        Keyboard.hideKeyboard(activity, activity.currentFocus)
        val fm = activity.supportFragmentManager
        fm.popBackStack()
    }


    override fun instance(): FullNamePresenter {
        return this
    }
}