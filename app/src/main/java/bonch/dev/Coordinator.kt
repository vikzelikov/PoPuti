package bonch.dev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import bonch.dev.view.getdriver.GetDriverView
import bonch.dev.view.getdriver.OfferPriceView
import bonch.dev.view.profile.ProfileView
import bonch.dev.view.regulardrive.RegularDriveView
import bonch.dev.view.signup.ConfirmPhoneFragment
import bonch.dev.view.signup.FullNameFragment

class Coordinator {


    companion object {

        private val TAG_FRAGMENT = "TAG_FRAGMENT"
        private var regularDrving: RegularDriveView? = null
        private var getDriver: GetDriverView? = null
        private var profile: ProfileView? = null

        fun replaceFragment(id: Int, bundle: Bundle, fm: FragmentManager) {
            when (id) {
                MAIN_FRAGMENT -> {
                    val fragment = MainFragment()
                    fragment.arguments = bundle

                    fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(TAG_FRAGMENT)
                        .commit()
                }

                DETAIL_RIDE_VIEW -> {
                    val fragment = DetailRideView()
                    fragment.arguments = bundle

                    fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(TAG_FRAGMENT)
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
                            ConfirmPhoneFragment(startHeight, screenHeight)
                        )
                        .addToBackStack(TAG_FRAGMENT)
                        .commit()
                }

                FULL_NAME_VIEW -> {
                    fm.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            FullNameFragment(startHeight, screenHeight)
                        )
                        .addToBackStack(TAG_FRAGMENT)
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


}