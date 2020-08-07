package bonch.dev.poputi.presentation.modules.driver.signup.steps.presenter

import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.poputi.domain.entities.driver.signup.SignupStep
import bonch.dev.poputi.domain.entities.driver.signup.Step

interface ISignupStepPresenter {

    fun instance(): SignupStepPresenter

    fun getCamera(fragment: Fragment)

    fun onActivityResult(fragment: Fragment, requestCode: Int, resultCode: Int, data: Intent?)

    fun getStepData(idStep: Step): SignupStep

}