package bonch.dev.poputi.data.repository.passenger.regular.ride

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface IRegularRidesRepository {

    fun getActiveRides(token: String, callback: DataHandler<ArrayList<RideInfo>?>)

    fun getArchiveRides(token: String, callback: DataHandler<ArrayList<RideInfo>?>)

    fun updateRideStatus(
        status: StatusRide,
        rideId: Int,
        token: String,
        callback: SuccessHandler
    )
}