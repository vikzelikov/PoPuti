package bonch.dev.domain.interactor.passenger.getdriver

import android.util.Log
import bonch.dev.data.repository.common.chat.IChatRepository
import bonch.dev.data.repository.passenger.getdriver.IGetDriverRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.passenger.getdriver.IGetDriverStorage
import bonch.dev.domain.entities.common.chat.MessageObject
import bonch.dev.domain.entities.common.ride.*
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.GeocoderHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.interfaces.SuggestHandler
import bonch.dev.presentation.modules.passenger.getdriver.GetDriverComponent
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point
import io.realm.RealmResults
import javax.inject.Inject

class GetDriverInteractor : IGetDriverInteractor {

    @Inject
    lateinit var getDriverRepository: IGetDriverRepository

    @Inject
    lateinit var chatRepository: IChatRepository

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
    override fun createRide(rideInfo: RideInfo, callback: SuccessHandler) {
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
                            saveRideId(id)
                            callback(true)
                        } else {
                            //error
                            callback(false)
                        }
                    }
                } else if (rideId != null) {
                    //Ok
                    saveRideId(rideId)
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


    override fun connectSocket(callback: SuccessHandler) {
        val rideId = getDriverStorage.getRideId()
        val token = profileStorage.getToken()

        if (token != null && rideId != -1) {
            getDriverRepository.connectSocket(rideId, token) { isSuccess ->
                if (isSuccess) {
                    callback(true)
                } else {
                    //retry connect
                    getDriverRepository.connectSocket(rideId, token, callback)
                }
            }
        } else callback(false)
    }


    override fun subscribeOnChangeRide(callback: DataHandler<String?>) {
        getDriverRepository.subscribeOnChangeRide(callback)
    }


    override fun subscribeOnOfferPrice(callback: DataHandler<String?>) {
        getDriverRepository.subscribeOnOfferPrice(callback)
    }


    override fun subscribeOnDeleteOffer(callback: DataHandler<String?>) {
        getDriverRepository.subscribeOnDeleteOffer(callback)
    }


    override fun connectChatSocket(callback: SuccessHandler) {
        val rideId = ActiveRide.activeRide?.rideId
        val token = profileStorage.getToken()

        if (token != null && rideId != null) {
            chatRepository.connectSocket(rideId, token) { isSuccess ->
                if (isSuccess) {
                    callback(true)
                } else {
                    //retry connect
                    chatRepository.connectSocket(rideId, token, callback)
                }
            }
        } else callback(false)
    }


    override fun subscribeOnChat(callback: DataHandler<String?>) {
        val userId = profileStorage.getUserId()

        if (userId != -1) {
            chatRepository.subscribeOnChat { data, _ ->
                val message = Gson().fromJson(data, MessageObject::class.java)?.message
                if (message?.author?.id != userId) callback(data, null)
            }
        }
    }


    override fun disconnectSocket() {
        getDriverRepository.disconnectSocket()
        chatRepository.disconnectSocket()
    }


    override fun saveRideId(rideId: Int) {
        ActiveRide.activeRide?.rideId = rideId
        getDriverStorage.saveRideId(rideId)
    }


    override fun getRideId(): Int {
        return getDriverStorage.getRideId()
    }


    override fun removeRideId() {
        getDriverStorage.removeRideId()
    }


    //update ride status with server
    override fun updateRideStatus(status: StatusRide, callback: SuccessHandler) {
        val rideId = ActiveRide.activeRide?.rideId
        val token = profileStorage.getToken()

        if (rideId != null && token != null) {
            getDriverRepository.updateRideStatus(status, rideId, token) { isSuccess ->
                if (!isSuccess) {
                    //retry request
                    getDriverRepository.updateRideStatus(status, rideId, token) {
                        callback(it)
                    }
                } else callback(true)
            }
        } else {
            Log.d(
                "UPDATE_RIDE",
                "Update ride with server failed (rideId: $rideId, token: $token)"
            )
            callback(false)
        }
    }


    override fun setDriverInRide(userId: Int, callback: SuccessHandler) {
        val rideId = ActiveRide.activeRide?.rideId
        val token = profileStorage.getToken()

        if (rideId != null && token != null) {
            getDriverRepository.setDriverInRide(userId, rideId, token) { isSuccess ->
                if (!isSuccess) {
                    //retry request
                    getDriverRepository.setDriverInRide(userId, rideId, token) {
                        callback(it)
                    }
                } else callback(true)
            }
        } else {
            Log.d(
                "LINK_DRIVER_TO_RIDE",
                "Link driver to ride with server failed (rideId: $rideId, token: $token)"
            )
            callback(false)
        }
    }


    override fun sendReason(textReason: String, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val rideId = ActiveRide.activeRide?.rideId

        if (token != null && rideId != null) {
            getDriverRepository.sendCancelReason(rideId, textReason, token) { isSuccess ->
                if (isSuccess) callback(true)
                else getDriverRepository.sendCancelReason(rideId, textReason, token, callback)
            }
        } else callback(false)
    }


    override fun deleteOffer(offerId: Int, callback: SuccessHandler) {
        val token = profileStorage.getToken()

        if (token != null) {
            getDriverRepository.deleteOffer(offerId, token) { isSuccess ->
                if (isSuccess) callback(true)
                else getDriverRepository.deleteOffer(offerId, token, callback)
            }
        } else callback(false)
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
                        tempAdr.point =
                            AddressPoint(
                                p.latitude,
                                p.longitude
                            )

                    }
                }

                result.add(tempAdr)
            }

            result
        }
    }


}