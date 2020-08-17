package bonch.dev.poputi.domain.interactor.passenger.regular.ride

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler

interface IRegularRidesInteractor {

    fun getActiveRides(callback: DataHandler<ArrayList<RideInfo>?>)


    fun getArchiveRides(callback: DataHandler<ArrayList<RideInfo>?>)

}