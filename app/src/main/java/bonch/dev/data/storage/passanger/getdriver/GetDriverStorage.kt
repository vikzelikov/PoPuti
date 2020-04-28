package bonch.dev.data.storage.passanger.getdriver

import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults


/**
 * Realm DataBase not supported filtered data with variable `Case`, if string is not England (2020 y)
 * */


class GetDriverStorage : IGetDriverStorage {

    private var realm: Realm? = null
    private var realmCash: Realm? = null

    fun initRealm() {
//        if (realm == null) {
//            val context = createRidePresenter.createRideView.context
//
//            if (context != null) {
//                Realm.init(context)
//                val config = RealmConfiguration.Builder()
//                    .name(Constants.CASH_RIDE_REALM_NAME)
//                    .deleteRealmIfMigrationNeeded()
//                    .build()
//                realm = Realm.getInstance(config)
//            }
//        }
    }


    fun initRealmCash() {
//        if (realmCash == null) {
//            val context = createRidePresenter.createRideView.context
//
//            if (context != null) {
//                Realm.init(context)
//                val config = RealmConfiguration.Builder()
//                    .name(Constants.CASH_REQUEST_REALM_NAME)
//                    .deleteRealmIfMigrationNeeded()
//                    .build()
//                realmCash = Realm.getInstance(config)
//            }
//        }
    }


    //Cash suggest request
    override fun saveCashRequest(cashRequest: ArrayList<Ride>) {
        cashRequest.forEach {
            it.isCashed = true
        }

        realmCash?.executeTransactionAsync {
            it.insertOrUpdate(cashRequest)
        }
    }


    override fun getCashRequest(request: String): RealmResults<Ride>? {
        return realmCash?.where(Ride::class.java)
            ?.beginsWith("address", request.trim(), Case.INSENSITIVE)?.findAll()
    }


    //Cash suggest for offer
    override fun saveCashSuggest(cashRides: ArrayList<Ride>) {
        realm?.executeTransaction {
            cashRides.forEach {
                realm?.insertOrUpdate(it)
            }
        }
    }


    override fun getCashSuggest(): RealmResults<Ride>? {
        return realm?.where(Ride::class.java)?.findAll()
    }


    override fun deleteCashSuggest(ride: Ride) {
        realm?.executeTransaction {
            ride.deleteFromRealm()
        }
    }

}