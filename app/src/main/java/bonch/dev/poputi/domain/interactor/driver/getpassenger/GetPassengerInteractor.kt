package bonch.dev.poputi.domain.interactor.driver.getpassenger

import android.util.Log
import bonch.dev.poputi.data.repository.common.chat.IChatRepository
import bonch.dev.poputi.data.repository.driver.getpassenger.IGetPassengerRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.driver.getpassenger.IGetPassengerStorage
import bonch.dev.poputi.domain.entities.common.chat.MessageObject
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import bonch.dev.poputi.presentation.modules.driver.getpassenger.GetPassengerComponent
import com.google.gson.Gson
import javax.inject.Inject

class GetPassengerInteractor : IGetPassengerInteractor {

    @Inject
    lateinit var getPassengerRepository: IGetPassengerRepository

    @Inject
    lateinit var chatRepository: IChatRepository

    @Inject
    lateinit var getPassengerStorage: IGetPassengerStorage

    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    override fun connectSocket(callback: SuccessHandler) {
        val rideId = ActiveRide.activeRide?.rideId
        val token = profileStorage.getToken()

        if (token != null && rideId != null) {
            getPassengerRepository.connectSocket(rideId, token) { isSuccess ->
                if (isSuccess) {
                    callback(true)
                } else {
                    //retry connect
                    getPassengerRepository.connectSocket(rideId, token, callback)
                }
            }
        } else callback(false)
    }


    override fun subscribeOnGetOffers(callback: DataHandler<String?>) {
        getPassengerRepository.subscribeOnGetOffers(callback)
    }


    override fun subscribeOnChangeRide(callback: DataHandler<String?>) {
        getPassengerRepository.subscribeOnChangeRide(callback)
    }


    override fun subscribeOnDeleteOffer(callback: DataHandler<String?>) {
        getPassengerRepository.subscribeOnDeleteOffer(callback)
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
        getPassengerRepository.disconnectSocket()
        chatRepository.disconnectSocket()
    }


    //update ride status with server
    override fun updateRideStatus(status: StatusRide, callback: SuccessHandler) {
        val rideId = ActiveRide.activeRide?.rideId
        val token = profileStorage.getToken()

        if (rideId != null && token != null) {
            getPassengerRepository.updateRideStatus(status, rideId, token) { isSuccess ->
                if (isSuccess) {
                    //success
                    callback(true)
                } else {
                    //retry request
                    getPassengerRepository.updateRideStatus(status, rideId, token) {
                        callback(it)
                    }
                }
            }
        } else {
            Log.d(
                "UPDATE_RIDE",
                "Update ride with server failed (rideId: $rideId, token: $token)"
            )
            callback(false)
        }
    }


    override fun setDriverInRide(callback: SuccessHandler) {
        val userId = profileStorage.getUserId()
        val rideId = ActiveRide.activeRide?.rideId
        val token = profileStorage.getToken()

        if (userId != -1 && rideId != null && token != null) {
            getPassengerRepository.setDriverInRide(userId, rideId, token) { isSuccess ->
                if (isSuccess) {
                    //success
                    callback(true)
                } else {
                    //retry request
                    getPassengerRepository.setDriverInRide(userId, rideId, token) {
                        callback(it)
                    }
                }
            }
        } else {
            Log.d(
                "LINK_DRIVER_TO_RIDE",
                "Link driver to ride with server failed (rideId: $rideId, token: $token)"
            )
            callback(false)
        }
    }


    override fun saveRideId(rideId: Int) {
        getPassengerStorage.saveRideId(rideId)
    }


    override fun getRideId() = getPassengerStorage.getRideId()


    override fun removeRideId() {
        getPassengerStorage.removeRideId()
    }


    override fun saveWaitTimestamp() {
        getPassengerStorage.saveWaitTimestamp()
    }


    override fun getWaitTimestamp() = getPassengerStorage.getWaitTimestamp()


    override fun offerPrice(price: Int, rideId: Int, callback: DataHandler<Offer?>) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            getPassengerRepository.offerPrice(price, rideId, userId, token) { data, error ->
                if (data != null && error == null) {
                    //success
                    callback(data, null)
                }
            }
        }
    }


    override fun getUserId(): Int {
        return profileStorage.getUserId()
    }


    override fun getNewOrder(callback: DataHandler<ArrayList<RideInfo>?>) {
        getPassengerRepository.getNewOrders { orders, error ->
            if (error != null) {
                //retry request
                getPassengerRepository.getNewOrders { listOrders, _ ->
                    if (listOrders != null) callback(listOrders, null)
                    else callback(null, "Error")
                }
            } else if (orders != null) callback(orders, null)
        }
    }


    override fun sendReason(textReason: String, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val rideId = ActiveRide.activeRide?.rideId

        if (token != null && rideId != null) {
            getPassengerRepository.sendCancelReason(rideId, textReason, token) { isSuccess ->
                if (isSuccess) callback(true)
                else getPassengerRepository.sendCancelReason(rideId, textReason, token, callback)
            }
        } else callback(false)
    }


    override fun deleteOffer(offerId: Int, callback: SuccessHandler) {
        val token = profileStorage.getToken()

        if (token != null) {
            getPassengerRepository.deleteOffer(offerId, token) { isSuccess ->
                if (isSuccess) callback(true)
                else getPassengerRepository.deleteOffer(offerId, token, callback)
            }
        } else callback(false)
    }

}