package bonch.dev.presenter.signup

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Coordinator.previousFragment
import bonch.dev.utils.Coordinator.replaceFragment

class SignupPresenter {

    fun clickNextBtn(id: Int, fm: FragmentManager) {
        when (id) {
            FULL_NAME_VIEW -> {
                replaceFragment(MAIN_FRAGMENT, Bundle(), fm)
            }
        }
    }


    fun clickNextBtn(id: Int, startHeight: Int, screenHeight: Int, fm: FragmentManager) {
        when (id) {
            PHONE_VIEW -> {
                replaceFragment(CONFIRM_PHONE_VIEW, startHeight, screenHeight, fm)
            }

            CONFIRM_PHONE_VIEW -> {
                replaceFragment(FULL_NAME_VIEW, startHeight, screenHeight, fm)
            }
        }
    }


    fun clickBackBtn(fm: FragmentManager) {
        previousFragment(fm)
    }
}