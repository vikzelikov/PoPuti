package bonch.dev.domain.interactor

import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler

interface IBaseInteractor {

    fun getToken(): String?

    fun getUserId(): Int

    fun isCheckoutDriver(): Boolean

    fun validateAccount(callback: SuccessHandler)

    fun getRideId(): Int

    fun getRide(rideId: Int, callback: DataHandler<RideInfo?>)

}