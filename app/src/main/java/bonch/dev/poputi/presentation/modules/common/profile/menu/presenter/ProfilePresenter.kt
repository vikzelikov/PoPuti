package bonch.dev.poputi.presentation.modules.common.profile.menu.presenter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyStep
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.city.SelectCityView
import bonch.dev.poputi.presentation.modules.common.profile.language.ChangeLangView
import bonch.dev.poputi.presentation.modules.common.profile.me.view.ProfileDetailView
import bonch.dev.poputi.presentation.modules.common.profile.menu.view.IProfileView
import bonch.dev.poputi.presentation.modules.common.profile.passenger.verification.VerifyPresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.verification.VerifyView
import bonch.dev.poputi.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.poputi.route.MainRouter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject


class ProfilePresenter : BasePresenter<IProfileView>(), IProfilePresenter {

    @Inject
    lateinit var profileInteractor: IProfileInteractor

    val PROFILE_DETAIL_VIEW = 11
    val CONFIRM_PERSON = 12
    val CHANGE_LANG = 14
    val LANG = "LANG"
    private val IS_SHOW_POPUP = "IS_SHOW_POPUP"


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun getProfile() {

        val context = App.appComponent.getContext()
        if (!NetworkUtil.isNetworkConnected(context)) {
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }

        val p = CacheProfile.profile

        if (p != null) getView()?.setProfile(p)
        else {
            profileInteractor.getProfile { profileData, _ ->
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        if (profileData?.firstName == null) {
                            Handler().postDelayed({
                                getProfile()
                            }, 1000)
                        }

                        profileData?.let {
                            CacheProfile.profile = it

                            getView()?.setProfile(it)
                        }
                    }
                }
                mainHandler.post(myRunnable)
            }
        }
    }


    override fun showFullProfile(fragment: Fragment) {
        CacheProfile.profile?.let {
            val context = App.appComponent.getContext()
            val intent = Intent(context, ProfileDetailView::class.java)
            fragment.startActivityForResult(intent, PROFILE_DETAIL_VIEW)
        }
    }


    override fun checkoutAccount(isPassanger: Boolean, fragment: Fragment) {
        val context = App.appComponent.getContext().applicationContext
        if (NetworkUtil.isNetworkConnected(context)) {
            if (isPassanger) {
                val intent = Intent(context, DriverSignupActivity::class.java)
                fragment.startActivityForResult(intent, 1)
            } else {
                //redirect to passanger
                profileInteractor.saveCheckoutDriver(false)
                MainRouter.showView(
                    R.id.show_main_passenger_fragment,
                    getView()?.getNavHost(),
                    null
                )

                getView()?.stopSearchOrders()
            }
        } else {
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }
    }


    override fun checkModerateVerification(photos: Array<Photo>) {
        val list = arrayListOf<Photo>()

        photos.forEach {
            if (it.imgName == VerifyPresenter.getTitlePhoto(VerifyStep.SELF_PHOTO_PASSPORT) && it.isVerify == 0) {
                list.add(it)
            }

            if (it.imgName == VerifyPresenter.getTitlePhoto(VerifyStep.PASSPORT_ADDRESS_PHOTO) && it.isVerify == 0) {
                list.add(it)
            }

            if (list.size == 2) {
                getView()?.showModerateIcon()
            }
        }
    }


    override fun profileDataResult(data: Intent) {
        val isShowPopup = data.getBooleanExtra(IS_SHOW_POPUP, false)

        if (isShowPopup) {
            val res = App.appComponent.getContext().resources
            getView()?.showNotification(res.getString(R.string.dataSaved))
        }
    }


    override fun addBankCard() {
        MainRouter.showView(R.id.banking, getView()?.getNavHost(), null)
    }


    override fun confirmPerson(fragment: Fragment) {
        val context = App.appComponent.getContext()
        val intent = Intent(context, VerifyView::class.java)
        fragment.startActivityForResult(intent, CONFIRM_PERSON)
    }


    override fun showSelectCity(fragment: Fragment) {
        val context = App.appComponent.getContext()
        val intent = Intent(context, SelectCityView::class.java)
        fragment.startActivityForResult(intent, Geo.SELECT_CITY)
    }


    override fun showProfits() {
        MainRouter.showView(R.id.profits, getView()?.getNavHost(), null)
    }


    override fun showStoryOrders(isPassanger: Boolean) {
        val key = "IS_PASSENGER"
        val bundle = Bundle()
        bundle.putBoolean(key, isPassanger)

        MainRouter.showView(R.id.story_passenger, getView()?.getNavHost(), bundle)
    }


    override fun changeLang(fragment: Fragment) {
        val context = App.appComponent.getContext()
        val intent = Intent(context, ChangeLangView::class.java)
        fragment.startActivityForResult(intent, CHANGE_LANG)
    }


    override fun checkOnboarding(isForPassenger: Boolean) {
        val key = "IS_PASSENGER"
        val bundle = Bundle()
        bundle.putBoolean(key, isForPassenger)

        if (isForPassenger) {
            if (!profileInteractor.getOnboardingPassenger())
                MainRouter.showView(R.id.show_onboarding, getView()?.getNavHost(), bundle)

        } else {
            if (!profileInteractor.getOnboardingDriver())
                MainRouter.showView(R.id.show_onboarding_d, getView()?.getNavHost(), bundle)
        }
    }


    override fun showRating() {
        CacheProfile.profile?.let {
            MainRouter.showView(R.id.rating_passenger, getView()?.getNavHost(), null)
        }
    }


    override fun showCarInfo() {
        CacheProfile.profile?.let {
            MainRouter.showView(R.id.car_info_driver, getView()?.getNavHost(), null)
        }
    }


    override fun showSupport(fragment: Fragment) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts("mailto", "poputi@bonch.dev", null)
        )
        fragment.startActivity(Intent.createChooser(emailIntent, "Send email..."), null)
//        MainRouter.showView(R.id.support, getView()?.getNavHost(), null)
    }


    override fun logout() {
        MainRouter.showView(R.id.show_phone, getView()?.getNavHost(), null)
    }


    override fun instance(): ProfilePresenter {
        return this
    }


    override fun getMyCity(): Address? {
        return profileInteractor.getMyCity()
    }


    override fun onDestroy() {
        profileInteractor.closeRealm()
    }

}