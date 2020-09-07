package bonch.dev.poputi.presentation.modules.common.onboarding.view

import bonch.dev.poputi.presentation.interfaces.IBaseView

interface IOnboardingView : IBaseView {

    fun setData(title: String, img: Int, step: Int)

    fun finishOnboarding()

}