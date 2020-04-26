package bonch.dev.data

import android.content.Context
import androidx.preference.PreferenceManager
import bonch.dev.App
import bonch.dev.domain.utils.Constants

class MainRepository {

     fun getToken(): String? {
        val pref = App.appComponent.getSharedPref()
        return pref.getString(Constants.ACCESS_TOKEN, null)
    }
}