package bonch.dev.domain.interactor.passanger.getdriver

import android.util.Log
import bonch.dev.data.repository.passanger.getdriver.IGetDriverRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.passanger.getdriver.IGetDriverStorage
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.passanger.getdriver.*
import bonch.dev.presentation.interfaces.NotificationHandler
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import com.yandex.mapkit.geometry.Point
import io.realm.RealmResults
import javax.inject.Inject

class GetDriverInteractor : IGetDriverInteractor {

    @Inject
    lateinit var getDriverRepository: IGetDriverRepository

    @Inject
    lateinit var getDriverStorage: IGetDriverStorage

    @Inject
    lateinit var profileStorage: IProfileStorage

    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    //NETWORK
    override fun requestGeocoder(point: Point, callback: GeocoderHandler) {
        getDriverRepository.requestGeocoder(point) { address, responsePoint ->
            callback(address, responsePoint)
        }
    }


    override fun requestSuggest(
        query: String,
        userLocationPoint: Point?,
        callback: SuggestHandler
    ) {
        getDriverRepository.requestSuggest(query, userLocationPoint) {
            callback(it)
        }
    }


    //create ride with server
    override fun createRide(rideInfo: RideInfo, callback: NotificationHandler) {
        val userId = profileStorage.getUserId()
        val token = profileStorage.getToken()


        if (token != null && userId != -1) {
            //set userId
            rideInfo.userId = userId

            getDriverRepository.createRide(rideInfo, token) { ride, error ->
                val rideId = ride?.rideId

                if (error != null) {
                    //retry request
                    getDriverRepository.createRide(rideInfo, token) { rideObj, _ ->
                        val id = rideObj?.rideId

                        if (id != null) {
                            //Ok
                            getDriverStorage.saveRideId(id)
                            callback(true)
                        } else {
                            //error
                            callback(false)
                        }
                    }
                } else if (rideId != null) {
                    //Ok
                    getDriverStorage.saveRideId(rideId)
                    callback(true)
                }
            }
        } else {
            //error
            Log.d(
                "CREATE_RIDE",
                "Create ride with server failed (userId: $userId, token: $token)"
            )
            callback(false)
        }
    }


    //update ride status with server
    override fun updateRideStatus(status: StatusRide) {
        val rideId = getDriverStorage.getRideId()
        val token = profileStorage.getToken()

        if (rideId != -1 && token != null) {
            getDriverRepository.updateRideStatus(status, rideId, token) { _, error ->
                if (error != null) {
                    //retry request
                    getDriverRepository.updateRideStatus(status, rideId, token) { _, _ -> }
                }
            }
        } else {
            Log.d(
                "UPDATE_RIDE",
                "Update ride with server failed (rideId: $rideId, token: $token)"
            )
        }
    }


    override fun linkDriverToRide(driverId: Int) {
        val rideId = getDriverStorage.getRideId()
        val token = profileStorage.getToken()

        if (rideId != -1 && token != null) {
            getDriverRepository.linkDriverToRide(driverId, rideId, token) { _, error ->
                if (error != null) {
                    //retry request
                    getDriverRepository.linkDriverToRide(driverId, rideId, token) { _, _ -> }
                }
            }
        } else {
            Log.d(
                "LINK_DRIVER_TO_RIDE",
                "Link driver to ride with server failed (rideId: $rideId, token: $token)"
            )
        }
    }


    override fun getNewDriver(callback: NewDriver) {
        getDriverRepository.getNewDriver {
            callback(it)
        }
    }


    //STORAGE
    override fun initRealm() {
        getDriverStorage.initRealm()
    }


    override fun getCashRequest(query: String): ArrayList<Address>? {
        return removeRealmObservable(getDriverStorage.getCashRequest(query))
    }


    override fun getCashSuggest(): ArrayList<Address>? {
        return removeRealmObservable(getDriverStorage.getCashSuggest())
    }


    override fun saveCashRequest(cashRequest: ArrayList<Address>) {
        getDriverStorage.saveCashRequest(cashRequest)
    }


    override fun saveCashSuggest(cashAddresses: ArrayList<Address>) {
        getDriverStorage.saveCashSuggest(cashAddresses)
    }


    override fun deleteCashSuggest(address: Address) {
        getDriverStorage.deleteCashSuggest(address)
    }


    override fun saveDriver(driver: Driver) {
        //TODO send driver to server
    }


    override fun removeDriver() {
        //TODO cancel ride
    }


    override fun closeRealm() {
        getDriverStorage.closeRealm()
    }


    private fun removeRealmObservable(list: RealmResults<Address>?): ArrayList<Address>? {
        return if (list == null) {
            null
        } else {
            val result = arrayListOf<Address>()
            for (i in 0 until list.size) {
                val tempAdr = Address()

                list[i]?.let {
                    tempAdr.id = it.id
                    tempAdr.address = it.address
                    tempAdr.description = it.description
                    tempAdr.uri = it.uri
                    tempAdr.isCashed = it.isCashed

                    val p = it.point
                    if (p != null) {
                        tempAdr.point = AddressPoint(p.latitude, p.longitude)

                    }
                }

                result.add(tempAdr)
            }

            result
        }
    }


}