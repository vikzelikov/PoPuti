package bonch.dev.presenter.profile

import androidx.preference.PreferenceManager
import bonch.dev.utils.Constants
import bonch.dev.view.profile.ProfileDetailView

class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) {

    fun logout() {
        val activity = profileDetailView
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        editor.remove(Constants.ACCESS_TOKEN)
        editor.apply()


        //exitProcess(-1)
    }
}