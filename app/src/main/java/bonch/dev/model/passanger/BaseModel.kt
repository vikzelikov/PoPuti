package bonch.dev.model.passanger

import android.content.Context
import androidx.preference.PreferenceManager
import bonch.dev.model.passanger.getdriver.pojo.Driver
import bonch.dev.utils.Constants

class BaseModel {

     fun getToken(context: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getString(Constants.ACCESS_TOKEN, null)
    }
}