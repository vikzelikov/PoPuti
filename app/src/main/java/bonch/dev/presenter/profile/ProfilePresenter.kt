package bonch.dev.presenter.profile

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import bonch.dev.model.profile.ProfileModel
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.PROFILE_DATA
import bonch.dev.utils.Coordinator.openActivity
import bonch.dev.view.profile.ProfileView
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
            val bitmap =
                BitmapFactory.decodeByteArray(profileData.imgUser, 0, profileData.imgUser!!.size)
            Glide.with(profileView.context!!).load(bitmap)
                .apply(RequestOptions().centerCrop().circleCrop())
                .into(imgUser)
        }
    }


    override fun getContext(): Context? {
        return profileView.context
    }
}