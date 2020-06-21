package bonch.dev.data.repository

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.interfaces.DataHandler


interface IMainRepository {

    fun getRide(rideId: Int, callback: DataHandler<RideInfo?>)

}