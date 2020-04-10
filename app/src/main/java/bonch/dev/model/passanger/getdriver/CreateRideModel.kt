package bonch.dev.model.passanger.getdriver

import bonch.dev.model.passanger.getdriver.pojo.Ride
import bonch.dev.presenter.passanger.getdriver.CreateRidePresenter
import bonch.dev.utils.Constants
import com.yandex.mapkit.geometry.Point
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

/**
 * Realm DataBase not supported filtered data with variable `Case`, if string is not England (2020 y)
 * */

class CreateRideModel(private val createRidePresenter: CreateRidePresenter) {

    private var autocomplete: Autocomplete? = null
    private var geocoder: Geocoder? = null
    var realm: Realm? = null
    var realmCash: Realm? = null


    fun initRealm() {
        if (realm == null) {
            val context = createRidePresenter.createRideView.context

            if (context != null) {
                Realm.init(context)
                val config = RealmConfiguration.Builder()
                    .name(Constants.CASH_RIDE_REALM_NAME)
                    .build()
                realm = Realm.getInstance(config)
            }
        }
    }


    fun initRealmCash() {
        if (realmCash == null) {
            val context = createRidePresenter.createRideView.context

            if (context != null) {
                Realm.init(context)
                val config = RealmConfiguration.Builder()
                    .name(Constants.CASH_REQUEST_REALM_NAME)
                    .build()
                realmCash = Realm.getInstance(config)
            }
        }
    }


    //Suggest (autocomplete)
    fun requestSuggest(query: String, userLocationPoint: Point?) {
        if (autocomplete == null) {
            autocomplete = Autocomplete(this, userLocationPoint)
        }

        autocomplete!!.requestSuggest(query)
    }


    fun responseSuggest(suggestResult: ArrayList<Ride>) {
        createRidePresenter.responseSuggest(suggestResult)
    }


    //Cash suggest for offer
    fun saveCashSuggest(cashRides: ArrayList<Ride>) {
        realm?.executeTransaction {
            cashRides.forEach {
                realm?.insertOrUpdate(it)
            }
        }
    }


    fun deleteCashSuggest(ride: Ride) {
        realm?.executeTransaction {
            ride.deleteFromRealm()
        }
    }


    fun getCashSuggest(): RealmResults<Ride>? {
        return realm?.where(Ride::class.java)?.findAll()
    }


    //Geocoder
    fun requestGeocoder(point: Point) {
        if (geocoder == null) {
            geocoder = Geocoder(this)
        }

        geocoder!!.request(point)
    }


    fun responseGeocoder(address: String?, point: Point?) {
        createRidePresenter.responseGeocoder(address, point)
    }


    //Cash suggest request
    fun saveCashRequest(cashRequest: ArrayList<Ride>) {
        cashRequest.forEach {
            it.isCashed = true
        }

        realmCash?.executeTransactionAsync {
            it.insertOrUpdate(cashRequest)
        }
    }


    fun getCashRequest(request: String): RealmResults<Ride>? {
        return realmCash?.where(Ride::class.java)
            ?.beginsWith("address", request.trim(), Case.INSENSITIVE)?.findAll()
    }
}