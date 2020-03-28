package bonch.dev.presenter.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.utils.Constants
import bonch.dev.view.profile.ProfileDetailView
import kotlin.system.exitProcess

class ProfileDetailPresenter(val profileDetailView: ProfileDetailView) {

    fun logout() {
        val activity = profileDetailView
        val pref = activity.getPreferences(Context.MODE_PRIVATE)

        val editor = pref.edit()

        val ed =  profileDetailView.getPreferences(AppCompatActivity.MODE_PRIVATE)
        println(ed.contains(Constants.ACCESS_TOKEN))

        editor.remove(Constants.ACCESS_TOKEN)
        editor.apply()



        //exitProcess(-1)
    }
}