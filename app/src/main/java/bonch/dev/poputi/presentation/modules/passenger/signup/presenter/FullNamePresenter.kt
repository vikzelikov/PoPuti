package bonch.dev.poputi.presentation.modules.passenger.signup.presenter

import androidx.fragment.app.FragmentActivity
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.passenger.signup.DataSignup
import bonch.dev.poputi.domain.interactor.passenger.signup.ISignupInteractor
import bonch.dev.domain.utils.Keyboard
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.passenger.signup.view.ContractView
import bonch.dev.poputi.route.MainRouter
import javax.inject.Inject

class FullNamePresenter : BasePresenter<ContractView.IFullNameView>(),
    ContractPresenter.IFullNamePresenter {

    @Inject
    lateinit var signupInteractor: ISignupInteractor


    init {
        SignupComponent.passengerSignupComponent?.inject(this)
    }


    override fun doneSignup() {
        getView()?.showLoading()

        signupInteractor.initRealm()

        //if there is data from old app version
        signupInteractor.resetProfile()
        //save Data:
        //save token
        saveToken()
        //save and send profile data to server
        saveProfileData { isSuccess ->
            if (isSuccess) {
                //clear data
                SignupComponent.passengerSignupComponent = null
                DataSignup.phone = null
                DataSignup.token = null
                DataSignup.userId = null

                //next transition
                MainRouter.showView(
                    R.id.main_passenger_fragment,
                    getView()?.getNavHost(),
                    null
                )

                getView()?.hideLoading()

            } else {
                getView()?.showNotification(
                    App.appComponent.getContext().getString(R.string.errorSystem)
                )
            }
        }
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


    private fun saveProfileData(callback: SuccessHandler) {
        val profileData = getView()?.getProfileData()
        val token = DataSignup.token
        val userId = DataSignup.userId
        profileData?.phone = DataSignup.phone

        if (token != null && userId != null && profileData != null) {
            //remote save
            signupInteractor.saveProfile(userId, token, profileData)
            callback(true)
        } else callback(false)
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