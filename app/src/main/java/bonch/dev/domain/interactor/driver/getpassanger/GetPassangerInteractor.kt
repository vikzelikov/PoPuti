package bonch.dev.domain.interactor.driver.getpassanger

import android.util.Log
import bonch.dev.data.repository.driver.getpassanger.IGetPassangerRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.domain.entities.driver.getpassanger.SelectOrder
import bonch.dev.presentation.modules.driver.getpassanger.GetPassangerComponent
import javax.inject.Inject

class GetPassangerInteractor : IGetPassangerInteractor {

    @Inject
    lateinit var getPassangerRepository: IGetPassangerRepository

    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        GetPassangerComponent.getPassangerComponent?.inject(this)
    }


    //update ride status with server
    override fun updateRideStatus(status: StatusRide) {
        val rideId = SelectOrder.order?.id
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
        val rideId = SelectOrder.order?.id
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


    override fun getNewOrder(callback: NewOrder) {
        getPassangerRepository.getNewOrder {
            callback(it)
        }
    }

}