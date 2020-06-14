package bonch.dev.data.storage.passenger.getdriver

import bonch.dev.domain.entities.common.ride.Address
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