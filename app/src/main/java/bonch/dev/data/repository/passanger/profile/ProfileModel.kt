package bonch.dev.data.repository.passanger.profile

import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.presentation.presenter.passanger.profile.IProfilePresenter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.PROFILE_REALM_NAME
import io.realm.Realm
import io.realm.RealmConfiguration

class ProfileModel(val profilePresenter: IProfilePresenter) {


    var realm: Realm? = null


    fun initRealm() {
        if (realm == null) {
            val context = profilePresenter.getContext()

            if(context != null){
                Realm.init(context)
                val config = RealmConfiguration.Builder()
                    .name(PROFILE_REALM_NAME)
                    .deleteRealmIfMigrationNeeded()
                    .build()
                realm = Realm.getInstance(config)
            }
        }
    }


    fun getProfileData(): Profile? {
        var profileData: Profile? = null
        val realmResult = realm!!.where(Profile::class.java).findAll()

        if (realmResult != null && realmResult.isNotEmpty()) {
            profileData = Profile()

            for(i in realmResult.indices){
                val realmData = realmResult[i]
                profileData.firstName = realmData!!.firstName
                profileData.lastName = realmData.lastName
                profileData.phone = realmData.phone
                profileData.email = realmData.email
                profileData.imgUser = realmData.imgUser
                profileData.isNotificationsEnable = realmData.isNotificationsEnable
                profileData.isCallsEnable = realmData.isCallsEnable
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
        val activity = profilePresenter.getContext()
        val pref = getDefaultSharedPreferences(activity?.applicationContext)
        val editor = pref.edit()

        editor.remove(Constants.ACCESS_TOKEN)
        editor.apply()
    }

}