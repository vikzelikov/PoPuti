package bonch.dev.poputi.domain.interactor

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler

interface IBaseInteractor {

    fun getToken(): String?

    fun getUserId(): Int

    fun isCheckoutDriver(): Boolean

    fun validateAccount(callback: DataHandler<Profile?>)

    fun getRideId(): Int

    fun getRide(rideId: Int, callback: DataHandler<RideInfo?>)

}