package bonch.dev

import androidx.fragment.app.FragmentManager
import bonch.dev.Constant.Companion.ADD_BANK_CARD_VIEW
import bonch.dev.Constant.Companion.CONFIRM_PHONE_VIEW
import bonch.dev.Constant.Companion.DETAIL_RIDE_VIEW
import bonch.dev.Constant.Companion.FULL_NAME_VIEW
import bonch.dev.Constant.Companion.MAIN_FRAGMENT
import bonch.dev.Constant.Companion.OFFER_PRICE_VIEW
import bonch.dev.view.MainFragment
import bonch.dev.view.getdriver.AddBankCardView
import bonch.dev.view.getdriver.DetailRideView
import bonch.dev.view.getdriver.OfferPriceView
import bonch.dev.view.signup.ConfirmPhoneFragment
import bonch.dev.view.signup.FullNameFragment

class Coordinator (private val supportFragmentManager: FragmentManager) {


    fun replaceFragment(id: Int) {
        when (id) {
            MAIN_FRAGMENT -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MainFragment())
                    .addToBackStack(null)
                    .commit()
            }

            DETAIL_RIDE_VIEW -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DetailRideView())
                    .addToBackStack(null)
                    .commit()
            }

            OFFER_PRICE_VIEW -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, OfferPriceView())
                    .addToBackStack(null)
                    .commit()
            }

            ADD_BANK_CARD_VIEW -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddBankCardView())
                    .addToBackStack(null)
                    .commit()
            }
        }

    }


    fun replaceFragment(id: Int, startHeight: Int, screenHeight: Int) {
        when(id) {
            CONFIRM_PHONE_VIEW -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ConfirmPhoneFragment(startHeight, screenHeight))
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