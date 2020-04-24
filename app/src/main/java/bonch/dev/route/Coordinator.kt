package bonch.dev.route

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.R
import bonch.dev.domain.utils.Constants
import bonch.dev.domain.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.domain.utils.Constants.CONFIRM_PHONE_VIEW
import bonch.dev.domain.utils.Constants.CREATE_RIDE_VIEW
import bonch.dev.domain.utils.Constants.DRIVER_SIGNUP
import bonch.dev.domain.utils.Constants.DRIVER_SIGNUP_CAR_INFO
import bonch.dev.domain.utils.Constants.DRIVER_SIGNUP_CHECK_PHOTO
import bonch.dev.domain.utils.Constants.DRIVER_SIGNUP_DOCS_VIEW
import bonch.dev.domain.utils.Constants.DRIVER_SIGNUP_STEP_VIEW
import bonch.dev.domain.utils.Constants.DRIVER_SIGNUP_TABLE_DOCS
import bonch.dev.domain.utils.Constants.FULL_NAME_VIEW
import bonch.dev.domain.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.domain.utils.Constants.MAIN_FRAGMENT
import bonch.dev.domain.utils.Constants.OFFER_PRICE_VIEW
import bonch.dev.domain.utils.Constants.PHONE_VIEW
import bonch.dev.domain.utils.Constants.PROFILE_CHECK_PHOTO
import bonch.dev.domain.utils.Constants.PROFILE_FULL_VIEW
import bonch.dev.presentation.modules.passanger.MainFragment
import bonch.dev.presentation.driver.signup.*
import bonch.dev.presentation.modules.passanger.getdriver.addcard.view.AddBankCardView
import bonch.dev.presentation.modules.passanger.getdriver.chat.view.ChatView
import bonch.dev.presentation.modules.passanger.getdriver.orfferprice.view.OfferPriceView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.CreateRideView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.GetDriverView
import bonch.dev.presentation.modules.passanger.profile.view.CheckPhotoView
import bonch.dev.presentation.modules.passanger.profile.view.ProfileDetailView
import bonch.dev.presentation.modules.passanger.signup.view.ConfirmPhoneView
import bonch.dev.presentation.modules.passanger.signup.view.FullNameView
import bonch.dev.presentation.modules.passanger.signup.view.PhoneView


object Coordinator {

    fun replaceFragment(id: Int, bundle: Bundle?, fm: FragmentManager) {
        when (id) {
            MAIN_FRAGMENT -> {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                val fragment =
                    MainFragment()
                fragment.arguments = bundle

                fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment, MAIN_FRAGMENT.toString())
                    .commit()

            }

            FULL_NAME_VIEW -> {
                val fragment =
                    FullNameView()
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
                val fragment =
                    PhoneView()
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
                val fragment =
                    GetDriverView()
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
                val fragment =
                    CreateRideView()
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
                val fragment = bonch.dev.presentation.driver.signup.CheckPhotoView()
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

            CONFIRM_PHONE_VIEW -> {
                val fragment = ConfirmPhoneView()
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


    fun replaceFragment(
        id: Int,
        fm: FragmentManager,
        startHeight: Int,
        screenHeight: Int,
        bundle: Bundle?
    ) {
        when (id) {
            CONFIRM_PHONE_VIEW -> {
                val fragment =
                    ConfirmPhoneView(
                    )
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

            PROFILE_FULL_VIEW -> {
                val intent = Intent(context, ProfileDetailView::class.java)
                fragment.startActivityForResult(intent, PROFILE_FULL_VIEW)
            }

            DRIVER_SIGNUP -> {
                val intent = Intent(context, DriverSignupActivity::class.java)
                fragment.startActivityForResult(intent, DRIVER_SIGNUP)
            }
        }
    }


    fun showPassangerView(){
        //TODO
    }


    fun showDriverView(fm: FragmentManager){
            val fragment = bonch.dev.presentation.driver.MainFragment()
            fm.beginTransaction()
                .replace(R.id.fragment_container, fragment, MAIN_FRAGMENT.toString())
                .commit()
    }


    fun showChat(activity: Activity) {
        val context = activity.applicationContext
        val intent = Intent(context, ChatView::class.java)
        activity.startActivityForResult(intent, 1)
    }


    fun showCheckPhoto(context: Context, activity: Activity, img: String) {
        val intent = Intent(context, CheckPhotoView::class.java)
        intent.putExtra(Constants.PHOTO, img)
        activity.startActivityForResult(intent, PROFILE_CHECK_PHOTO)
    }


    fun showCarInfoSuggest(
        context: Context,
        fragment: Fragment,
        isCarName: Boolean,
        carName: String?
    ) {
        val intent = Intent(context, SuggestView::class.java)
        intent.putExtra(Constants.BOOL_DATA, isCarName)
        carName?.let {
            intent.putExtra(Constants.STRING_DATA, it)
        }
        fragment.startActivityForResult(intent, 1)
    }


    fun testtt(){

    }


    fun previousFragment(fm: FragmentManager) {
        fm.popBackStack()
    }

}


