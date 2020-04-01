package bonch.dev.model.driver.signup

import androidx.preference.PreferenceManager
import bonch.dev.model.driver.signup.pojo.DocsRealm
import bonch.dev.presenter.driver.signup.TableDocsPresenter
import bonch.dev.utils.Constants
import bonch.dev.view.driver.signup.DriverSignupActivity
import io.realm.Realm
import io.realm.RealmConfiguration


class DriverSignupModel(val tableDocsPresenter: TableDocsPresenter) {


    var realm: Realm? = null


    private fun initRealm() {
        if (realm == null) {
            val context = tableDocsPresenter.tableDocsView.context

            if (context != null) {
                Realm.init(context)
                val config = RealmConfiguration.Builder()
                    .name(Constants.DRIVER_DOCS_REALM_NAME)
                    .build()
                realm = Realm.getInstance(config)
            }
        }
    }


    fun getDocsDB(): ArrayList<DocsRealm> {
        initRealm()

        val list: ArrayList<DocsRealm> = arrayListOf()
        val realmResult = realm!!.where(DocsRealm::class.java)!!.findAll()

        if (realmResult.isNotEmpty()) {
            for (i in realmResult!!.indices) {
                list.add(DocsRealm(i, realmResult[i]!!.imgDocs))
            }
        }

        return list
    }


    fun saveDocs(listDocs: ArrayList<DocsRealm>) {
        initRealm()

        realm!!.executeTransaction {
            it.insertOrUpdate(listDocs)
        }
    }


    fun removeStartStatus() {
        val activity = tableDocsPresenter.tableDocsView.activity as DriverSignupActivity
        val pref = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext)
        val editor = pref.edit()

        editor.remove(Constants.DRIVER_SIGNUP_START.toString())
        editor.apply()
    }

}