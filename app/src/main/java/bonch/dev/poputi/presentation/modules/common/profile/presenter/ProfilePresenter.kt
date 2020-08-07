package bonch.dev.poputi.presentation.modules.common.profile.presenter

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.domain.utils.NetworkUtil
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import bonch.dev.poputi.presentation.modules.common.profile.view.IProfileView
import bonch.dev.poputi.presentation.modules.common.profile.view.ProfileDetailView
import bonch.dev.poputi.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.poputi.route.MainRouter
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


    override fun getProfile() {
        var profile: Profile?

        val context = App.appComponent.getContext()
        if (!NetworkUtil.isNetworkConnected(context)) {
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }

        profileInteractor.initRealm()

        profileInteractor.getProfileRemote { profileData, _ ->
            val mainHandler = Handler(Looper.getMainLooper())
            val myRunnable = Runnable {
                kotlin.run {
                    profile = profileData
                    profile = profileData

                    //try to get from locate storage
                    if (profile == null) {
                        profile = profileInteractor.getProfileLocal()
                    }

                    profile?.let {
                        getView()?.setProfile(it)
                    }
                }
            }
            mainHandler.post(myRunnable)
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
            getView()?.setProfile(it)
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