package bonch.dev.presenter.passanger.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.View
import bonch.dev.MainActivity
import bonch.dev.model.passanger.profile.ProfileModel
import bonch.dev.model.passanger.profile.pojo.Profile
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.DRIVER_SIGNUP
import bonch.dev.utils.Constants.PROFILE_DATA
import bonch.dev.utils.Coordinator.openActivity
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.view.passanger.profile.ProfileView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.profile_fragment.view.*

class ProfilePresenter(val profileView: ProfileView) : IProfilePresenter {


    private var profileModel: ProfileModel? = null
    private var handlerAnimation: Handler? = null
    var root: View? = null


    init {
        if (profileModel == null) {
            profileModel = ProfileModel(this)
        }
    }


    fun getProfileDataDB() {
        profileModel?.initRealm()

        val profileData = profileModel?.getProfileData()

        profileData?.let {
            setProfileData(it)
        }
    }


    fun getFullProfile() {
        val context = profileView.context
        openActivity(Constants.PROFILE_FULL_VIEW, context!!, profileView)
    }


    fun checkoutToDriver() {
        openActivity(DRIVER_SIGNUP, profileView.requireContext(), profileView)
    }


    fun profileDataResult(data: Intent) {
        val isShowPopup = data.getBooleanExtra(Constants.IS_SHOW_POPUP, false)

        if (isShowPopup) {
            showNotifications()
        }

        val profileData = data.getParcelableExtra<Profile>(PROFILE_DATA)
        setProfileData(profileData)
    }


    private fun setProfileData(profileData: Profile?) {
        val nameUser = root!!.name_user
        val imgUser = root!!.img_user
        nameUser.text = profileData?.firstName.plus(" ").plus(profileData?.lastName)

        if (profileData?.imgUser != null) {
            val imageUri = Uri.parse(profileData.imgUser)
            Glide.with(profileView.context!!).load(imageUri)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(imgUser)
        }
    }


    private fun showNotifications() {
        val view = profileView.view?.notification

        handlerAnimation?.removeCallbacksAndMessages(null)
        handlerAnimation = Handler()
        view?.translationY = 0.0f
        view?.alpha = 0.0f

        view?.animate()
            ?.setDuration(500L)
            ?.translationY(100f)
            ?.alpha(1.0f)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    handlerAnimation?.postDelayed({ hideNotifications() }, 1000)
                }
            })

    }


    private fun hideNotifications() {
        val view = profileView.view?.notification

        view?.animate()?.setDuration(500L)?.translationY(-100f)?.alpha(0.0f)
    }


    fun logout() {
        val fm = (profileView.activity as MainActivity).supportFragmentManager
        replaceFragment(Constants.PHONE_VIEW, null, fm)
    }


    override fun getContext(): Context? {
        return profileView.context
    }
}