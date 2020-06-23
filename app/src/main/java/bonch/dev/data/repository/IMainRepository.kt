package bonch.dev.data.repository

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.entities.common.ride.StatusRide
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler


interface IMainRepository {

    fun getRide(rideId: Int, callback: DataHandler<RideInfo?>)

    fun connectSocket(rideId: Int, token: String, callback: SuccessHandler)

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

}