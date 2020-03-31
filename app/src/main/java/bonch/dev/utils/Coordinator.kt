package bonch.dev.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.R
import bonch.dev.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.utils.Constants.CREATE_RIDE_VIEW
import bonch.dev.utils.Constants.DRIVER_SIGNUP
import bonch.dev.utils.Constants.DRIVER_SIGNUP_CAR_INFO
import bonch.dev.utils.Constants.DRIVER_SIGNUP_CHECK_PHOTO
import bonch.dev.utils.Constants.DRIVER_SIGNUP_DOCS_VIEW
import bonch.dev.utils.Constants.DRIVER_SIGNUP_STEP_VIEW
import bonch.dev.utils.Constants.DRIVER_SIGNUP_TABLE_DOCS
import bonch.dev.utils.Constants.FULL_NAME_VIEW
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.OFFER_PRICE_VIEW
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Constants.PROFILE_FULL
import bonch.dev.view.MainFragment
import bonch.dev.view.driver.signup.*
import bonch.dev.view.passanger.getdriver.AddBankCardView
import bonch.dev.view.passanger.getdriver.CreateRideView
import bonch.dev.view.passanger.getdriver.GetDriverView
import bonch.dev.view.passanger.getdriver.OfferPriceView
import bonch.dev.view.passanger.profile.ProfileDetailView
import bonch.dev.view.passanger.signup.ConfirmPhoneView
import bonch.dev.view.passanger.signup.FullNameView
import bonch.dev.view.passanger.signup.PhoneView


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
                val fragment = PhoneView()

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
                    .commit()
            }

            FULL_NAME_VIEW -> {
                val fragment = FullNameView()
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
                val fragment = PhoneView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        fragment,
                        PHONE_VIEW.toString()
                    )
                    .commit()
            }

            GET_DRIVER_VIEW -> {
                val fragment = GetDriverView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        fragment,
                        GET_DRIVER_VIEW.toString()
                    )
                    .commit()
            }

            CREATE_RIDE_VIEW -> {
                val fragment = CreateRideView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        fragment,
                        CREATE_RIDE_VIEW.toString()
                    )
                    .commit()
            }


            DRIVER_SIGNUP_DOCS_VIEW -> {
                val fragment = ListDocsView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container_driver_signup,
                        fragment,
                        DRIVER_SIGNUP_DOCS_VIEW.toString()
                    )
                    .commit()
            }


            DRIVER_SIGNUP_STEP_VIEW -> {
                val fragment = SignupStepView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container_driver_signup,
                        fragment,
                        DRIVER_SIGNUP_STEP_VIEW.toString()
                    )
                    .commit()
            }


            DRIVER_SIGNUP_CAR_INFO -> {
                val fragment = CarInfoView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container_driver_signup,
                        fragment,
                        DRIVER_SIGNUP_CAR_INFO.toString()
                    )
                    .commit()
            }


            DRIVER_SIGNUP_CHECK_PHOTO -> {
                val fragment = CheckPhotoView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container_driver_signup,
                        fragment,
                        DRIVER_SIGNUP_CHECK_PHOTO.toString()
                    )
                    .commit()
            }


            DRIVER_SIGNUP_TABLE_DOCS -> {
                val fragment = TableDocsView()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(
                        R.id.fragment_container_driver_signup,
                        fragment,
                        DRIVER_SIGNUP_TABLE_DOCS.toString()
                    )
                    .commit()
            }
        }
    }


    fun replaceFragment(
        id: Int,
        fm: FragmentManager,
        startHeight: Int,
        screenHeight: Int,
        bundle: Bundle?
    ) {
        when (id) {
            CONFIRM_PHONE_VIEW -> {
                val fragment = ConfirmPhoneView(startHeight, screenHeight)
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

            PROFILE_FULL -> {
                val intent = Intent(context, ProfileDetailView::class.java)
                fragment.startActivityForResult(intent, PROFILE_FULL)
            }

            DRIVER_SIGNUP -> {
                val intent = Intent(context, DriverSignupActivity::class.java)
                fragment.startActivityForResult(intent, DRIVER_SIGNUP)
            }

        }
    }


    fun previousFragment(fm: FragmentManager) {
        fm.popBackStack()
    }

}


