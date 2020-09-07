package bonch.dev.poputi.presentation.modules.common.onboarding.presenter

import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.CommonComponent
import bonch.dev.poputi.presentation.modules.common.onboarding.view.IOnboardingView
import javax.inject.Inject

class OnboardingPresenter : BasePresenter<IOnboardingView>(), IOnboardingPresenter {


    @Inject
    lateinit var profileInteractor: IProfileInteractor

    var step = 0

    init {
        CommonComponent.commonComponent?.inject(this)
    }


    override fun nextStep(isForPassenger: Boolean) {
        val r = App.appComponent.getContext()

        step++

        if (isForPassenger) {
            when (step) {
                1 -> getView()?.setData(r.getString(R.string.onboarding1Passenger), 1, step)
                2 -> getView()?.setData(r.getString(R.string.onboarding2Passenger), 2, step)
                3 -> getView()?.setData(r.getString(R.string.onboarding3Passenger), 3, step)
                4 -> getView()?.setData(r.getString(R.string.onboarding4Passenger), 4, step)
                5 -> {
                    //end
                    profileInteractor.saveOnboardingPassenger()

                    getView()?.finishOnboarding()
                }
            }
        } else {
            when (step) {
                1 -> getView()?.setData(r.getString(R.string.onboarding1Driver), 1, step)
                2 -> getView()?.setData(r.getString(R.string.onboarding2Driver), 2, step)
                3 -> getView()?.setData(r.getString(R.string.onboarding3Driver), 3, step)
                4 -> {
                    //end
                    profileInteractor.saveOnboardingDriver()

                    getView()?.finishOnboarding()
                }
            }
        }
    }


    override fun instance() = this

}