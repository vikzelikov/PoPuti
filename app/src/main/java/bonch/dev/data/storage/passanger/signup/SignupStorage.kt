package bonch.dev.data.storage.passanger.signup

class SignupStorage : ISignupStorage {

    override fun saveToken() {

    }

    override fun saveProfileData() {

    }

    private fun saveToken(accessToken: String) {
//        val activity = signupPresenter.fragment.activity as MainActivity
//        val pref = getDefaultSharedPreferences(activity.applicationContext)
//        val editor = pref.edit()
//        editor.putString(ACCESS_TOKEN, accessToken)
//        editor.apply()
    }


//    fun saveFullName(profileData: Profile) {
//        val context = signupPresenter.fragment.context
//
//        if (context != null) {
//            Realm.init(context)
//            val config = RealmConfiguration.Builder()
//                .name(PROFILE_REALM_NAME)
//                .deleteRealmIfMigrationNeeded()
//                .build()
//            val realm = Realm.getInstance(config)
//
//
//            realm?.executeTransactionAsync({ bgRealm ->
//                bgRealm.insertOrUpdate(profileData)
//            }, {
//                realm.close()
//                //ok
//            }, {
//                realm.close()
//                //fail
//            })
//        }
//    }
}