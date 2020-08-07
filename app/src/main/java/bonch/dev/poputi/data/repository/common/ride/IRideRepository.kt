package bonch.dev.poputi.data.repository.common.ride

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler

interface IRideRepository {

    fun getRide(rideId: Int, callback: DataHandler<RideInfo?>)

}