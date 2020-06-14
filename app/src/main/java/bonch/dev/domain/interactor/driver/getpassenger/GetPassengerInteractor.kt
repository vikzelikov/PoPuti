package bonch.dev.domain.interactor.driver.getpassenger

import android.util.Log
import bonch.dev.data.repository.driver.getpassenger.IGetPassangerRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.driver.getpassenger.SelectOrder
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.modules.driver.getpassenger.GetPassengerComponent
import javax.inject.Inject

class GetPassengerInteractor : IGetPassengerInteractor {

    @Inject
    lateinit var getPassangerRepository: IGetPassangerRepository

    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        GetPassengerComponent.getPassengerComponent?.inject(this)
    }


    //update ride status with server
    override fun updateRideStatus(status: StatusRide) {
        val rideId = SelectOrder.order?.rideId
        val token = profileStorage.getToken()

        if (rideId != null && token != null) {
            getPassangerRepository.updateRideStatus(status, rideId, token) { _, error ->
                if (error != null) {
                    //retry request
                    getPassangerRepository.updateRideStatus(status, rideId, token) { _, _ -> }
                }
            }
        } else {
            Log.d(
                "UPDATE_RIDE",
                "Update ride with server failed (rideId: $rideId, token: $token)"
            )
        }
    }


    override fun linkDriverToRide() {
        val userId = profileStorage.getUserId()
        val rideId = SelectOrder.order?.rideId
        val token = profileStorage.getToken()

        if (userId != -1 && rideId != null && token != null) {
            getPassangerRepository.linkDriverToRide(userId, rideId, token) { _, error ->
                if (error != null) {
                    //retry request
                    getPassangerRepository.linkDriverToRide(userId, rideId, token) { _, _ -> }
                }
            }
        } else {
            Log.d(
                "LINK_DRIVER_TO_RIDE",
                "Link driver to ride with server failed (rideId: $rideId, token: $token)"
            )
        }
    }


    //todo протестить что если будет всегда  error (инет офф)
    override fun getNewOrder(callback: DataHandler<List<RideInfo>>) {
        getPassangerRepository.getNewOrders { orders, error ->
            if (error != null) {
                //retry request
                getPassangerRepository.getNewOrders { listOrders, _ ->
                    if (listOrders != null) callback(listOrders, null)
                }
            } else if (orders != null) callback(orders, null)
        }
    }

}