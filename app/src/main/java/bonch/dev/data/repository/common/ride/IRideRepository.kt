package bonch.dev.data.repository.common.ride

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.interfaces.DataHandler

interface IRideRepository {

    fun getRide(rideId: Int, callback: DataHandler<RideInfo?>)

}