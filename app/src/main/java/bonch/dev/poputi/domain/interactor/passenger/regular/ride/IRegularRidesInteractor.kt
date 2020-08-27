package bonch.dev.poputi.domain.interactor.passenger.regular.ride

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface IRegularRidesInteractor {

    fun getActiveRides(callback: DataHandler<ArrayList<RideInfo>?>)


    fun getArchiveRides(callback: DataHandler<ArrayList<RideInfo>?>)


    fun updateRideStatus(status: StatusRide, rideId: Int, callback: SuccessHandler)


    fun deleteRide(rideId: Int, callback: SuccessHandler)

}