package bonch.dev.data.storage.passanger.getdriver

import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import io.realm.RealmResults

interface IGetDriverStorage {

    fun saveCashRequest(cashRequest: ArrayList<Ride>)

    fun getCashRequest(request: String): RealmResults<Ride>?

    fun saveCashSuggest(cashRides: ArrayList<Ride>)

    fun deleteCashSuggest(ride: Ride)

    fun getCashSuggest(): RealmResults<Ride>?

}