package bonch.dev.presentation.modules.passanger.signup.presenter

import androidx.fragment.app.FragmentActivity
import bonch.dev.R
import bonch.dev.domain.entities.passanger.signup.DataSignup
import bonch.dev.domain.interactor.passanger.signup.ISignupInteractor
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.signup.SignupComponent
import bonch.dev.presentation.modules.passanger.signup.view.ContractView
import bonch.dev.route.MainRouter
import javax.inject.Inject

class FullNamePresenter : BasePresenter<ContractView.IFullNameView>(),
    ContractPresenter.IFullNamePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor


    init {
        SignupComponent.passangerSignupComponent?.inject(this)
    }


    override fun doneSignup() {
        signupInteractor.initRealm()
        //save Data:
        //save token
        saveToken()
        //save and send profile data to server
        sendProfileData()

        //clear data
        SignupComponent.passangerSignupComponent = null
        DataSignup.phone = null
        DataSignup.token = null

        //next transition
        MainRouter.showView(R.id.show_main_passanger_fragment, getView()?.getNavHost(), null)
    }


    override fun isNameEntered(): Boolean {
        var result = false

        if (getView() != null) {
            val firstName = getView()?.getFirstName()
            val lastName = getView()?.getLastName()

            if (!firstName.isNullOrEmpty() && firstName.length < 20 && !lastName.isNullOrEmpty() && lastName.length < 20) {
                getView()?.changeBtnEnable(true)
                result = true
            } else {
                getView()?.changeBtnEnable(false)
            }
        }

        return result
    }


    override fun sendProfileData() {
        val profileData = getView()?.getProfileData()
        val token = DataSignup.token
        profileData?.phone = DataSignup.phone

        if (token != null && profileData != null) {
            //local save
            signupInteractor.saveProfileData(profileData)

            //remote save
            signupInteractor.sendProfileData(token, profileData)
        }
    }


    override fun saveToken() {
        val token = DataSignup.token

        if (token != null) {
            signupInteractor.saveToken(token)
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