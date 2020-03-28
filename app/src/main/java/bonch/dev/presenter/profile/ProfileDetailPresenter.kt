package bonch.dev.presenter.profile

import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import bonch.dev.utils.Constants
import bonch.dev.view.profile.ProfileDetailView

class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) {

    fun logout() {
        val activity = profileDetailView
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        val ed =  profileDetailView.getPreferences(AppCompatActivity.MODE_PRIVATE)
        println(ed.contains(Constants.ACCESS_TOKEN))

        editor.remove(Constants.ACCESS_TOKEN)
        editor.apply()


        //exitProcess(-1)
    }
}