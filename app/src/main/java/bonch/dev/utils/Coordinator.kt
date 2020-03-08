package bonch.dev.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.R
import bonch.dev.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.OFFER_PRICE_VIEW
import bonch.dev.view.MainFragment
import bonch.dev.view.getdriver.AddBankCardView
import bonch.dev.view.getdriver.OfferPriceView
import bonch.dev.view.signup.ConfirmPhoneFragment
import bonch.dev.view.signup.FullNameFragment


object Coordinator {
    fun replaceFragment(id: Int, bundle: Bundle, fm: FragmentManager) {
        when (id) {
            MAIN_FRAGMENT -> {
                val fragment = MainFragment()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment, MAIN_FRAGMENT.toString())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    fun replaceFragment(id: Int, startHeight: Int, screenHeight: Int, fm: FragmentManager) {
        when (id) {
            CONFIRM_PHONE_VIEW -> {
                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        ConfirmPhoneFragment(startHeight, screenHeight),
                        CONFIRM_PHONE_VIEW.toString()
                    )
                    .addToBackStack(null)
                    .commit()
            }

            FULL_NAME_VIEW -> {
                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        FullNameFragment(startHeight, screenHeight),
                        FULL_NAME_VIEW.toString()
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


    fun openActivity(id: Int, context: Context, fragment: Fragment) {
        when (id) {
            OFFER_PRICE_VIEW -> {
                val intent = Intent(context, OfferPriceView::class.java)
                fragment.startActivityForResult(intent, OFFER_PRICE_VIEW)
            }

            ADD_BANK_CARD_VIEW -> {
                val intent = Intent(context, AddBankCardView::class.java)
                fragment.startActivityForResult(intent, ADD_BANK_CARD_VIEW)
            }

        }
    }


    fun previousFragment(fm: FragmentManager) {
        fm.popBackStack()
    }

}


