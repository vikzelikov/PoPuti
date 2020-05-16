package bonch.dev.data.storage.passanger.getdriver

import bonch.dev.domain.entities.passanger.getdriver.Address
import bonch.dev.domain.entities.passanger.getdriver.Ride
import bonch.dev.domain.entities.passanger.getdriver.RideInfo
import io.realm.RealmResults

interface IGetDriverStorage {

    fun initRealm()

    fun getCashRequest(request: String): RealmResults<Address>?

    fun getCashSuggest(): RealmResults<Address>?

    fun saveCashRequest(cashRequest: ArrayList<Address>)

    fun saveCashSuggest(cashAddresses: ArrayList<Address>)

    fun deleteCashSuggest(address: Address)

    fun saveRideId(id: Int)

    fun getRideId(): Int

    fun removeRideId()

    fun closeRealm()

}