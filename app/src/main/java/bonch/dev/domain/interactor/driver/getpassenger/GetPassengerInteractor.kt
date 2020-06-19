package bonch.dev.domain.interactor.driver.getpassenger

import android.util.Log
import bonch.dev.data.repository.driver.getpassenger.IGetPassengerRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import javax.inject.Inject

class GetPassengerInteractor : IGetPassengerInteractor {

    @Inject
    lateinit var getPassengerRepository: IGetPassengerRepository

    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
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


    override fun offerPrice(price: Int, rideId: Int, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            getPassengerRepository.offerPrice(price, rideId, userId, token) { isSuccess ->
                if (isSuccess) {
                    //success
                    callback(true)
                } else {
                    //retry request
                    getPassengerRepository.offerPrice(price, rideId, userId, token) {
                        callback(it)
                    }
                }
            }
        } else {
            //error
            callback(false)
        }
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

}