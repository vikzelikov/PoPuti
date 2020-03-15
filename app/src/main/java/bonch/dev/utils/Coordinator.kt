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
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.view.MainFragment
import bonch.dev.view.getdriver.AddBankCardView
import bonch.dev.view.getdriver.OfferPriceView
import bonch.dev.view.signup.ConfirmPhoneFragment
import bonch.dev.view.signup.FullNameFragment
import bonch.dev.view.signup.PhoneFragment


object Coordinator {

    fun addFragment(id: Int, fm: FragmentManager) {
        when (id) {
            MAIN_FRAGMENT -> {
                val fragment = MainFragment()

                fm.beginTransaction()
                    .add(R.id.fragment_container, fragment, MAIN_FRAGMENT.toString())
                    .commit()
            }

            PHONE_VIEW -> {
                val fragment = PhoneFragment()

                fm.beginTransaction()
                    .add(
                        R.id.fragment_container,
                        fragment,
                        PHONE_VIEW.toString()
                    )
                    .commit()
            }
        }
    }

    fun replaceFragment(id: Int, bundle: Bundle?, fm: FragmentManager) {
        when (id) {
            MAIN_FRAGMENT -> {
                val fragment = MainFragment()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment, MAIN_FRAGMENT.toString())
                    .addToBackStack(null)
                    .commit()
            }

            FULL_NAME_VIEW -> {
                val fragment = FullNameFragment()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        fragment,
                        FULL_NAME_VIEW.toString()
                    )
                    .addToBackStack(null)
                    .commit()
            }

            PHONE_VIEW -> {
                val fragment = PhoneFragment()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        fragment,
                        PHONE_VIEW.toString()
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    fun replaceFragment(
        id: Int,
        fm: FragmentManager,
        startHeight: Int,
        screenHeight: Int,
        bundle: Bundle
    ) {
        when (id) {
            CONFIRM_PHONE_VIEW -> {
                val fragment = ConfirmPhoneFragment(startHeight, screenHeight)
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        fragment,
                        CONFIRM_PHONE_VIEW.toString()
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


