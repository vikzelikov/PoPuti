package bonch.dev.model.driver.signup

import bonch.dev.model.driver.signup.pojo.DocsRealm
import bonch.dev.presenter.driver.signup.TableDocsPresenter
import bonch.dev.utils.Constants
import io.realm.Realm
import io.realm.RealmConfiguration

class DriverSignupModel(val tableDocsPresenter: TableDocsPresenter) {


    var realm: Realm? = null


    private fun initRealm() {
        if (realm == null) {
            val context = tableDocsPresenter.tableDocsView.context

            if(context != null){
                Realm.init(context)
                val config = RealmConfiguration.Builder()
                    .name(Constants.DRIVER_DOCS_REALM_NAME)
                    .build()
                realm = Realm.getInstance(config)
            }
        }
    }


    fun getDocs(): ArrayList<DocsRealm> {
        initRealm()

        val list: ArrayList<DocsRealm> = arrayListOf()
        val realmResult = realm!!.where(DocsRealm::class.java)!!.findAll()

        if (realmResult.isNotEmpty()) {
            for(i in realmResult!!.indices){
                list.add(DocsRealm(realmResult[i]!!.imgDocs))
            }
        }

        return list
    }


    fun saveDocs(listDocs: ArrayList<DocsRealm>) {
        initRealm()

        realm!!.executeTransactionAsync({ bgRealm ->
            bgRealm.insertOrUpdate(listDocs)
        }, {
            //ok
        }, {
            //fail
        })
    }

}