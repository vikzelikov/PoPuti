package bonch.dev.poputi.presentation.modules.common.onboarding.presenter

interface IOnboardingPresenter {

    fun nextStep(isForPassenger: Boolean)

    fun instance(): OnboardingPresenter

}