package bonch.dev.data

import android.content.Context
import androidx.preference.PreferenceManager
import bonch.dev.domain.utils.Constants

class MainRepository {

     fun getToken(context: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getString(Constants.ACCESS_TOKEN, null)
    }
}