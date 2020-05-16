package bonch.dev.presentation.modules.common.profile.presenter

import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.profile.Profile
import bonch.dev.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import bonch.dev.presentation.modules.common.profile.view.IProfileView
import bonch.dev.presentation.modules.common.profile.view.ProfileDetailView
import bonch.dev.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.route.MainRouter
import javax.inject.Inject

class ProfilePresenter : BasePresenter<IProfileView>(), IProfilePresenter {

    @Inject
    lateinit var profileInteractor: IProfileInteractor

    private val PROFILE_DETAIL_VIEW = 11
    private val IS_SHOW_POPUP = "IS_SHOW_POPUP"
    private val PROFILE_DATA = "PROFILE_DATA"


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun getProfileDataDB() {
        profileInteractor.initRealm()

        val profileData = profileInteractor.getProfileData()

        profileData?.let {
            getView()?.setProfileData(it)
        }
    }


    override fun showFullProfile(fragment: Fragment) {
        val context = App.appComponent.getContext()
        val intent = Intent(context, ProfileDetailView::class.java)
        fragment.startActivityForResult(intent, PROFILE_DETAIL_VIEW)
    }


    override fun checkoutAccount(isPassanger: Boolean, fragment: Fragment) {
        val context = App.appComponent.getContext().applicationContext
        if (NetworkUtil.isNetworkConnected(context)) {
            if (isPassanger) {
                if (profileInteractor.getDriverAccess()) {
                    //redirect to driver
                    MainRouter.showView(R.id.show_main_driver_fragment, getView()?.getNavHost(), null)
                } else {
                    //redirect to signup as driver
                    val intent = Intent(context, DriverSignupActivity::class.java)
                    fragment.startActivityForResult(intent, 1)
                }
            } else {
                //redirect to passanger
                MainRouter.showView(R.id.show_main_passanger_fragment, getView()?.getNavHost(), null)
            }
        } else {
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }

    }


    override fun profileDataResult(data: Intent) {
        val isShowPopup = data.getBooleanExtra(IS_SHOW_POPUP, false)

        if (isShowPopup) {
            val res = App.appComponent.getContext().resources
            getView()?.showNotification(res.getString(R.string.dataSaved))
        }

        val profileData = data.getParcelableExtra<Profile>(PROFILE_DATA)
        profileData?.let {
            getView()?.setProfileData(it)
        }
    }


    override fun logout() {
        MainRouter.showView(R.id.show_phone, getView()?.getNavHost(), null)
    }


    override fun instance(): ProfilePresenter {
        return this
    }


    override fun onDestroy() {
        profileInteractor.closeRealm()
    }

}