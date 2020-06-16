package bonch.dev.presentation.modules.driver.signup.steps.view

import bonch.dev.domain.entities.driver.signup.SignupStep
import bonch.dev.presentation.interfaces.IBaseView

interface ISignupStepView : IBaseView {

    fun setDataStep(stepData: SignupStep)

}