package bonch.dev.data.storage.common.profile

import bonch.dev.App
import bonch.dev.domain.entities.common.profile.Profile
import io.realm.Realm
import io.realm.RealmConfiguration

class ProfileStorage : IProfileStorage {

    private val PROFILE_REALM_NAME = "profile.realm"
    private val ID = "ID"
    private val DRIVER_ID = "DRIVER_ID"
    private val ACCESS_TOKEN = "ACCESS_TOKEN"
    private val DRIVER_ACCESS = "DRIVER_ACCESS"
    private val CHECKOUT_AS_DRIVER = "CHECKOUT_AS_DRIVER"


    private var realm: Realm? = null


    override fun initRealm() {
        if (realm == null) {
            val context = App.appComponent.getContext()

            Realm.init(context)
            val config = RealmConfiguration.Builder()
                .name(PROFILE_REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build()
            realm = Realm.getInstance(config)

        }
    }


    override fun saveToken(token: String) {
        val editor = App.appComponent.getSharedPref().edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
    }


    override fun getToken(): String? {
        val pref = App.appComponent.getSharedPref()
        return pref.getString(ACCESS_TOKEN, null)
    }


    override fun removeToken() {
        val editor = App.appComponent.getSharedPref().edit()
        editor.remove(ACCESS_TOKEN)
        editor.apply()
    }


    private fun saveUserId(id: Int) {
        val editor = App.appComponent.getSharedPref().edit()
        editor.putInt(ID, id)
        editor.apply()
    }


    override fun getUserId(): Int {
        val pref = App.appComponent.getSharedPref()
        return pref.getInt(ID, -1)
    }


    override fun saveDriverId(id: Int) {
        val editor = App.appComponent.getSharedPref().edit()
        editor.putInt(DRIVER_ID, id)
        editor.apply()
    }


    override fun getDriverId(): Int {
        val pref = App.appComponent.getSharedPref()
        return pref.getInt(DRIVER_ID, -1)
    }


    override fun getProfileData(): Profile? {
        var profile: Profile? = null

        val realmResult = realm?.where(Profile::class.java)?.findAll()

        if (realmResult != null && realmResult.isNotEmpty()) {
            profile = Profile()

            for (i in realmResult.indices) {
                val realmData = realmResult[i]
                realmData?.let {
                    profile.id = it.id
                    profile.firstName = it.firstName
                    profile.lastName = it.lastName
                    profile.phone = it.phone
                    profile.email = it.email
                    profile.imgUser = it.imgUser
                    profile.isNotificationsEnable = it.isNotificationsEnable
                    profile.isCallsEnable = it.isCallsEnable
                }
            }
        }

        return profile
    }


    override fun saveProfileData(profile: Profile) {
        //save User Id
        if (profile.id != 0) {
            saveUserId(profile.id)
        }

        //save full profile
        realm?.executeTransaction {
            it.insertOrUpdate(profile)
        }
    }


    override fun removeProfileData() {
        realm?.executeTransaction {
            it.where(Profile::class.java).findAll().deleteAllFromRealm()
        }
    }


    override fun saveDriverAccess() {
        val editor = App.appComponent.getSharedPref().edit()
        editor.putBoolean(DRIVER_ACCESS, true)
        editor.apply()
    }


    override fun getDriverAccess(): Boolean {
        val pref = App.appComponent.getSharedPref()
        return pref.getBoolean(DRIVER_ACCESS, false)
    }


    override fun saveCheckoutDriver(isDriver: Boolean) {
        val editor = App.appComponent.getSharedPref().edit()
        editor.putBoolean(CHECKOUT_AS_DRIVER, isDriver)
        editor.apply()
    }


    override fun isCheckoutDriver(): Boolean {
        val pref = App.appComponent.getSharedPref()
        return pref.getBoolean(CHECKOUT_AS_DRIVER, false)
    }


    override fun closeRealm() {
        realm?.close()
        realm = null
    }

}