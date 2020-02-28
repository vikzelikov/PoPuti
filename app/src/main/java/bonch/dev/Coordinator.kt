package bonch.dev

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import bonch.dev.Constant.Companion.CONFIRM_PHONE_VIEW
import bonch.dev.Constant.Companion.DETAIL_RIDE_VIEW
import bonch.dev.Constant.Companion.FULL_NAME_VIEW
import bonch.dev.Constant.Companion.MAIN_FRAGMENT
import bonch.dev.view.MainFragment
import bonch.dev.view.getdriver.DetailRideView
import bonch.dev.view.signup.ConfirmPhoneFragment
import bonch.dev.view.signup.FullNameFragment

class Coordinator(private val supportFragmentManager: FragmentManager) {


    fun replaceFragment(id: Int, bundle: Bundle) {
        when (id) {
            MAIN_FRAGMENT -> {
                val fragment = MainFragment()
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            DETAIL_RIDE_VIEW -> {
                val fragment = DetailRideView()
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }


    fun replaceFragment(id: Int, startHeight: Int, screenHeight: Int) {
        when (id) {
            CONFIRM_PHONE_VIEW -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        ConfirmPhoneFragment(startHeight, screenHeight)
                    )
                    .addToBackStack(null)
                    .commit()
            }

            FULL_NAME_VIEW -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FullNameFragment(startHeight, screenHeight))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


}