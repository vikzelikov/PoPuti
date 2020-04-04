package bonch.dev.presenter.passanger.profile

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import bonch.dev.model.passanger.profile.ProfileModel
import bonch.dev.model.passanger.profile.pojo.Profile
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.DRIVER_SIGNUP
import bonch.dev.utils.Constants.PROFILE_DATA
import bonch.dev.utils.Coordinator.openActivity
import bonch.dev.view.passanger.profile.ProfileView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.profile_fragment.view.*

class ProfilePresenter(val profileView: ProfileView) : IProfilePresenter {


    private var profileModel: ProfileModel? = null
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
        openActivity(Constants.PROFILE_FULL, context!!, profileView)
    }


    fun checkoutToDriver() {
        openActivity(DRIVER_SIGNUP, profileView.requireContext(), profileView)
    }


    fun profileDataResult(data: Intent) {
        val profileData = data.getParcelableExtra<Profile>(PROFILE_DATA)
        setProfileData(profileData)
    }


    private fun setProfileData(profileData: Profile?) {
        val nameUser = root!!.name_user
        val imgUser = root!!.img_user
        nameUser.text = profileData?.fullName

        if (profileData?.imgUser != null) {
            val imageUri = Uri.parse(profileData.imgUser)
            Glide.with(profileView.context!!).load(imageUri)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(imgUser)
        }
    }


    override fun getContext(): Context? {
        return profileView.context
    }
}