package bonch.dev.poputi.domain.interactor.passenger.regular.ride

import bonch.dev.poputi.data.repository.passenger.regular.ride.IRegularRidesRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import javax.inject.Inject

class RegularRidesInteractor : IRegularRidesInteractor {


    @Inject
    lateinit var regularRidesRepository: IRegularRidesRepository

    @Inject
    lateinit var profileStorage: IProfileStorage

    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)
    }


    override fun getActiveRides(callback: DataHandler<ArrayList<RideInfo>?>) {
        val token = profileStorage.getToken()

        if (token != null) {
            regularRidesRepository.getActiveRides(token) { rides, _ ->
                if (rides == null) {
                    regularRidesRepository.getActiveRides(token, callback)
                } else callback(rides, null)
            }
        } else callback(null, "error")
    }


    override fun getArchiveRides(callback: DataHandler<ArrayList<RideInfo>?>) {
        val token = profileStorage.getToken()

        if (token != null) {
            regularRidesRepository.getArchiveRides(token) { rides, _ ->
                if (rides == null) {
                    regularRidesRepository.getArchiveRides(token, callback)
                } else callback(rides, null)
            }
        } else callback(null, "error")
    }
}