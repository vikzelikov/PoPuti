package bonch.dev.model.profile

import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.presenter.profile.ProfileDetailPresenter
import bonch.dev.utils.Constants

class ProfileModel(val profileDetailPresenter: ProfileDetailPresenter) {


    fun getProfileData(): Profile {
        val profileData = Profile()
        val activity = profileDetailPresenter.profileDetailView
        val pref = getDefaultSharedPreferences(activity.applicationContext)

        profileData.fullName = pref.getString(Constants.FULL_NAME, "")
        profileData.phone = pref.getString(Constants.PHONE_NUMBER, "")
        profileData.email = pref.getString(Constants.EMAIL, "")

        return profileData
    }


    fun saveProfileData(profileData: Profile) {
        val activity = profileDetailPresenter.profileDetailView
        val pref = getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        editor.putString(Constants.FULL_NAME, profileData.fullName)
        editor.putString(Constants.PHONE_NUMBER, profileData.phone)
        editor.putString(Constants.EMAIL, profileData.email)
        editor.apply()
    }


    fun removeAccessToken() {
        val activity = profileDetailPresenter.profileDetailView
        val pref = getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        editor.remove(Constants.ACCESS_TOKEN)
        editor.apply()
    }

}