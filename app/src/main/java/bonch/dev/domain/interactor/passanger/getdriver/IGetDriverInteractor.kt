package bonch.dev.domain.interactor.passanger.getdriver

import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import com.yandex.mapkit.geometry.Point
import io.realm.RealmResults

interface IGetDriverInteractor {

    fun initRealm()

    fun requestGeocoder(point: Point)

    fun requestSuggest(query: String, userLocationPoint: Point?)

    fun getCashRequest(query: String): RealmResults<Ride>?

    fun getCashSuggest(): RealmResults<Ride>?

    fun saveCashRequest(cashRequest: ArrayList<Ride>)

    fun saveCashSuggest(cashRides: ArrayList<Ride>)

    fun deleteCashSuggest(ride: Ride)

    fun closeRealm()

}