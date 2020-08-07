package bonch.dev.poputi.presentation.modules.driver.signup.steps.view

import bonch.dev.poputi.domain.entities.driver.signup.SignupStep
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface ISignupStepView : IBaseView {

    fun setDataStep(stepData: SignupStep)

}