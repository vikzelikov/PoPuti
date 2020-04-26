package bonch.dev.data.storage.passanger.signup

import bonch.dev.App
import bonch.dev.data.repository.passanger.profile.pojo.Profile
import bonch.dev.domain.utils.Constants.ACCESS_TOKEN
import bonch.dev.domain.utils.Constants.PROFILE_REALM_NAME
import io.realm.Realm
import io.realm.RealmConfiguration

class SignupStorage : ISignupStorage {

    override fun saveToken(token: String) {
        val pref = App.appComponent.getSharedPref()
        val editor = pref.edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
    }

    override fun saveProfileData(profileData: Profile) {
        val context = App.appComponent.getContext()

        Realm.init(context)
        val config = RealmConfiguration.Builder()
            .name(PROFILE_REALM_NAME)
            .deleteRealmIfMigrationNeeded()
            .build()
        val realm = Realm.getInstance(config)


        realm?.executeTransactionAsync({ bgRealm ->
            bgRealm.insertOrUpdate(profileData)
        }, {
            realm.close()
            //ok
        }, {
            realm.close()
            //fail
        })
    }
}