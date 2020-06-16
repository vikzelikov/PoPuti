package bonch.dev.presentation.modules.passenger.signup.presenter

import androidx.fragment.app.FragmentActivity
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.passenger.signup.DataSignup
import bonch.dev.domain.interactor.passenger.signup.ISignupInteractor
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.modules.passenger.signup.SignupComponent
import bonch.dev.presentation.modules.passenger.signup.view.ContractView
import bonch.dev.route.MainRouter
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

                //next transition
                MainRouter.showView(
                    R.id.show_main_passenger_fragment,
                    getView()?.getNavHost(),
                    null
                )

                getView()?.hideLoading()

            } else {
                getView()?.showNotification(App.appComponent.getContext().getString(R.string.errorSystem))
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
        profileData?.phone = DataSignup.phone

        if (token != null && profileData != null) {
            //remote save
            signupInteractor.saveProfile(token, profileData) {
                callback(it)
            }
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