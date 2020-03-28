package bonch.dev.presenter.profile

import android.view.View
import bonch.dev.model.profile.ProfileModel
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.view.profile.ProfileDetailView
import kotlinx.android.synthetic.main.profile_detail_activity.view.*

class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) {

    private var profileModel: ProfileModel? = null

    init {
        if (profileModel == null) {
            profileModel = ProfileModel(this)
        }
    }


    fun getProfileData(root: View) {
        val profileData = profileModel?.getProfileData()

        profileData?.let {
            profileDetailView.setProfileData(root, it)
        }
    }


    fun saveProfileData(root: View) {
        val profileData = Profile()
        val fullName = root.full_name.text.toString().trim()
        val phone = root.phone_number.text.toString().trim()
        val email = root.email.text.toString().trim()

        if (fullName.isNotEmpty()) {
            profileData.fullName = fullName
        }

        if (phone.isNotEmpty()) {
            profileData.phone = phone
        }

        if (email.isNotEmpty()) {
            profileData.email = email
        }

        profileModel?.saveProfileData(profileData)
    }


    fun logout() {
        profileModel?.removeAccessToken()


        //exitProcess(-1)
    }
}