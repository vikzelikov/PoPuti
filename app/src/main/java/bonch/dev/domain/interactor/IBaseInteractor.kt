package bonch.dev.domain.interactor

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.interfaces.DataHandler

interface IBaseInteractor {

    fun getToken(): String?

    fun getUserId(): Int

    fun isCheckoutDriver(): Boolean

    fun validateAccount(callback: DataHandler<Int?>)

    fun getRideId(): Int

    fun getRide(rideId: Int, callback: DataHandler<RideInfo?>)

}