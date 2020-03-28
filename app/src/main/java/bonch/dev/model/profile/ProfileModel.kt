package bonch.dev.model.profile

import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import bonch.dev.model.profile.pojo.Profile
import bonch.dev.presenter.profile.ProfileDetailPresenter
import bonch.dev.utils.Constants
import io.realm.Realm
import io.realm.RealmConfiguration

class ProfileModel(val profileDetailPresenter: ProfileDetailPresenter) {


    var realm: Realm? = null

    fun initRealm() {
        if (realm == null) {
            val context = profileDetailPresenter.profileDetailView.applicationContext
            Realm.init(context)
            val config = RealmConfiguration.Builder()
                .name("profile.realm")
                .build()
            realm = Realm.getInstance(config)
        }
    }


    fun getProfileData(): Profile? {
        var profileData: Profile? = null
        val realmResult = realm!!.where(Profile::class.java).findAll()

        if (realmResult != null && realmResult.isNotEmpty()) {
            profileData = Profile()

            for(i in realmResult.indices){
                val realmData = realmResult[i]
                profileData.fullName = realmData!!.fullName
                profileData.phone = realmData.phone
                profileData.email = realmData.email
                profileData.imgUser = realmData.imgUser
            }
        }

        return profileData
    }


    fun saveProfileData(profileData: Profile) {
        realm?.executeTransactionAsync({ bgRealm ->
            bgRealm.insertOrUpdate(profileData)
        }, {
            //ok
        }, {
            //fail
        })
    }


    fun removeAccessToken() {
        val activity = profileDetailPresenter.profileDetailView
        val pref = getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        editor.remove(Constants.ACCESS_TOKEN)
        editor.apply()
    }

}