package bonch.dev.presentation.modules.passanger.signup.presenter

import androidx.fragment.app.FragmentActivity
import bonch.dev.domain.entities.passanger.signup.DataSignup
import bonch.dev.domain.interactor.passanger.signup.ISignupInteractor
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.view.ContractView
import bonch.dev.route.passanger.signup.ISignupRouter
import javax.inject.Inject

class FullNamePresenter : BasePresenter<ContractView.IFullNameView>(),
    ContractPresenter.IFullNamePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    @Inject
    lateinit var router: ISignupRouter


    init {
        SignupComponent.signupComponent?.inject(this)
    }


    override fun doneSignup() {
        //save Data:
        //save token
        saveToken()
        //save profile data (first name and last name)
        saveProfileData()
        //send profile data to server
        sendProfileData()

        //clear data
        SignupComponent.signupComponent = null
        DataSignup.phone = null
        DataSignup.token = null

        //next transition
        val nav = getView()?.getNavHost()
        router.showMainFragment(nav)
    }


    override fun isNameEntered(): Boolean {
        var result = false

        if (getView() != null) {
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


    override fun saveProfileData() {
        val profileData = getView()?.getProfileData()

        profileData?.let {
            if (it.firstName != null && it.lastName != null) {
                if (it.firstName!!.isNotEmpty() && it.lastName!!.isNotEmpty()) {
                    profileData.fullName = it.firstName.plus(" ").plus(it.lastName)
                }

                if (DataSignup.phone?.trim()!!.isNotEmpty()) {
                    profileData.phone = DataSignup.phone?.trim()
                }

                signupInteractor.saveProfileData(profileData)
            }
        }
    }


    override fun sendProfileData() {
        val profileData = getView()?.getProfileData()

        if (DataSignup.token != null && profileData != null) {
            signupInteractor.sendProfileData(DataSignup.token!!, profileData)
        }
    }


    override fun saveToken() {
        if (DataSignup.token != null) {
            signupInteractor.saveToken(DataSignup.token!!)
        }
    }


    override fun back(activity: FragmentActivity) {
        Keyboard.hideKeyboard(activity, activity.currentFocus)
        getView()?.getNavHost()?.popBackStack()
    }


    override fun instance(): FullNamePresenter {
        return this
    }
}