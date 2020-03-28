package bonch.dev.view.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import bonch.dev.R
import bonch.dev.presenter.profile.ProfileDetailPresenter
import bonch.dev.utils.Constants
import bonch.dev.utils.Keyboard
import com.google.gson.Gson
import kotlinx.android.synthetic.main.profile_detail_activity.view.*


class ProfileDetailView : AppCompatActivity() {

    private var profileDetailPresenter: ProfileDetailPresenter? = null

    init {
        if (profileDetailPresenter == null) {
            profileDetailPresenter = ProfileDetailPresenter(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_detail_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        setListeners(root)
    }


    private fun setListeners(root: View) {
        val logout = root.logout
        val backBtn = root.back_btn

        backBtn.setOnClickListener {
            //save data
            Keyboard.hideKeyboard(this, root)
            finish()
        }


        logout.setOnClickListener {
            //profileDetailPresenter?.logout()
            println(getToken())
        }
    }


    private fun saveToken(accessToken: String) {
        val pref = getDefaultSharedPreferences(applicationContext)
        val editor = pref.edit()
        editor.putString(Constants.ACCESS_TOKEN, Gson().toJson(accessToken))
        editor.apply()
    }


    private fun getToken(): String? {
        val pref = getDefaultSharedPreferences(applicationContext)
        return pref.getString(Constants.ACCESS_TOKEN, null)
    }
}
