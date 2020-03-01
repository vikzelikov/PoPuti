package bonch.dev.presenter.signup

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import bonch.dev.Constant.Companion.CONFIRM_PHONE_VIEW
import bonch.dev.Constant.Companion.FULL_NAME_VIEW
import bonch.dev.Constant.Companion.MAIN_FRAGMENT
import bonch.dev.Constant.Companion.PHONE_VIEW
import bonch.dev.Coordinator.Companion.previousFragment
import bonch.dev.Coordinator.Companion.replaceFragment
import bonch.dev.MainActivity

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