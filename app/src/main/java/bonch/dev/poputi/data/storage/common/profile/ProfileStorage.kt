package bonch.dev.poputi.data.storage.common.profile

import bonch.dev.poputi.App
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import io.realm.Realm
import io.realm.RealmConfiguration

class ProfileStorage : IProfileStorage {

    private val BANKING_REALM = "banking.realm"
    private val USER_ID = "USER_ID"
    private val DRIVER_ID = "DRIVER_ID"
    private val ACCESS_TOKEN = "ACCESS_TOKEN"
    private val DRIVER_ACCESS = "DRIVER_ACCESS"
    private val CHECKOUT_AS_DRIVER = "CHECKOUT_AS_DRIVER"


    private var bankingRealm: Realm? = null


    override fun initRealm() {
        if (bankingRealm == null) {
            val context = App.appComponent.getContext()
            Realm.init(context)

            val bankingConfig = RealmConfiguration.Builder()
                .name(BANKING_REALM)
                .deleteRealmIfMigrationNeeded()
                .build()
            bankingRealm = Realm.getInstance(bankingConfig)
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


    override fun saveUserId(id: Int) {
        val editor = App.appComponent.getSharedPref().edit()
        editor.putInt(USER_ID, id)
        editor.apply()
    }


    override fun getUserId(): Int {
        val pref = App.appComponent.getSharedPref()
        return pref.getInt(USER_ID, -1)
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


    override fun removeProfile() {
        val editor = App.appComponent.getSharedPref().edit()
        editor.remove(ACCESS_TOKEN)
        editor.remove(USER_ID)
        editor.remove(DRIVER_ID)
        editor.remove(DRIVER_ACCESS)
        editor.remove(CHECKOUT_AS_DRIVER)
        editor.apply()
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


    override fun saveBankCard(card: BankCard) {
        val list = getBankCards()
        list.forEach {
            bankingRealm?.executeTransaction { bgRealm ->
                it.isSelect = false
                bgRealm.insertOrUpdate(card)
            }
        }

        bankingRealm?.executeTransactionAsync {
            it.insertOrUpdate(card)
        }
    }


    override fun getBankCards(): ArrayList<BankCard> {
        val list = arrayListOf<BankCard>()
        val realmResult = bankingRealm?.where(BankCard::class.java)?.findAll()
        realmResult?.let { list.addAll(it) }
        return list
    }


    override fun deleteBankCard(card: BankCard) {
        val id = "id"
        bankingRealm?.executeTransaction {
            it.where(BankCard::class.java).equalTo(id, card.id).findAll().deleteAllFromRealm()
        }
    }


    override fun closeRealm() {
        bankingRealm?.close()
        bankingRealm = null
    }

}