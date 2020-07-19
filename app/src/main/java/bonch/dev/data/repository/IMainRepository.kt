package bonch.dev.data.repository

import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler


interface IMainRepository {

    fun connectSocket(rideId: Int, token: String, callback: SuccessHandler)

    fun subscribeOnChangeRide(callback: DataHandler<String?>)

    fun subscribeOnDeleteOffer(callback: DataHandler<String?>)

    fun disconnectSocket()

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )

    fun setDriverInRide(
        userId: Int,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )

    fun sendCancelReason(
        rideId: Int,
        textReason: String,
        token: String,
        callback: SuccessHandler
    )


    fun deleteOffer(
        offerId: Int,
        token: String,
        callback: SuccessHandler
    )

}
