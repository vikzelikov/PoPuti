package bonch.dev.data.storage.passenger.getdriver

import bonch.dev.App
import bonch.dev.domain.entities.common.ride.Address
import bonch.dev.domain.entities.common.ride.Offer
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults


/**
 * Realm DataBase not supported filtered data with variable `Case`, if string is not English (2020 y)
 * */


class GetDriverStorage : IGetDriverStorage {

    private val RIDE_ID = "RIDE_ID"
    private val CASH_RIDE_REALM_NAME = "cashride.realm"
    private val CASH_REQUEST_REALM_NAME = "cashrequest.realm"

    private var realm: Realm? = null
    private var realmCash: Realm? = null

    override fun initRealm() {
        val context = App.appComponent.getContext()

        if (realm == null) {
            Realm.init(context)
            val config = RealmConfiguration.Builder()
                .name(CASH_RIDE_REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build()
            realm = Realm.getInstance(config)

        }

        if (realmCash == null) {
            Realm.init(context)
            val config = RealmConfiguration.Builder()
                .name(CASH_REQUEST_REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build()
            realmCash = Realm.getInstance(config)
        }
    }


    override fun saveRideId(id: Int) {
        val pref = App.appComponent.getSharedPref()
        val editor = pref.edit()
        editor.putInt(RIDE_ID, id)
        editor.apply()
    }


    override fun getRideId(): Int {
        val pref = App.appComponent.getSharedPref()
        return pref.getInt(RIDE_ID, -1)
    }


    override fun removeRideId() {
        val pref = App.appComponent.getSharedPref()
        val editor = pref.edit()
        editor.remove(RIDE_ID)
        editor.apply()
    }


    //Cash suggest request
    override fun saveCashRequest(cashRequest: ArrayList<Address>) {
        cashRequest.forEach {
            it.isCashed = true
        }

        realmCash?.executeTransactionAsync {
            it.insertOrUpdate(cashRequest)
        }
    }


    override fun getCashRequest(request: String): RealmResults<Address>? {
        val typeQuery = "address"
        return realmCash?.where(Address::class.java)
            ?.beginsWith(typeQuery, request.trim(), Case.INSENSITIVE)?.findAll()
    }


    //Cash suggest for offer
    override fun saveCashSuggest(cashAddresses: ArrayList<Address>) {
        realm?.executeTransaction {
            cashAddresses.forEach {
                realm?.insertOrUpdate(it)
            }
        }
    }


    override fun getCashSuggest(): RealmResults<Address>? {
        return realm?.where(Address::class.java)?.findAll()
    }


    override fun deleteCashSuggest(address: Address) {
        val id = "id"
        realm?.executeTransaction {
            it.where(Address::class.java).equalTo(id, address.id).findAll().deleteAllFromRealm()
        }
    }


    override fun closeRealm() {
        realm?.close()
        realmCash?.close()

        realm = null
        realmCash = null
    }
}