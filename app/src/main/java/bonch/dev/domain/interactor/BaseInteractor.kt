package bonch.dev.domain.interactor

import bonch.dev.App
import bonch.dev.data.repository.IMainRepository
import bonch.dev.data.repository.passenger.signup.ISignupRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.data.storage.passenger.getdriver.IGetDriverStorage
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import javax.inject.Inject

class BaseInteractor : IBaseInteractor {

    @Inject
    lateinit var signupRepository: ISignupRepository

    @Inject
    lateinit var mainRepository: IMainRepository

    @Inject
    lateinit var profileStorage: IProfileStorage

    @Inject
    lateinit var getDriverStorage: IGetDriverStorage


    init {
        App.appComponent.inject(this)
    }


    override fun validateAccount(callback: SuccessHandler) {
        val token = profileStorage.getToken()
        if (token != null) {
            signupRepository.getUserId(token) { data, error ->
                if (data != null && error == null) {
                    callback(true)
                } else callback(false)
            }
        } else callback(false)
    }


    //for authorization
    override fun getToken(): String? {
        return profileStorage.getToken()
    }


    override fun getUserId(): Int {
        return profileStorage.getUserId()
    }


    override fun getRideId(): Int {
        return getDriverStorage.getRideId()
    }


    override fun getRide(rideId: Int, callback: DataHandler<RideInfo?>) {
        mainRepository.getRide(rideId) { data, error ->
            if (data == null && error != null) {
                //retry request
                mainRepository.getRide(rideId) { dataRide, _ ->
                    if (dataRide == null) {
                        //error
                        callback(null, "error")
                    } else {
                        //success
                        callback(dataRide, null)
                    }
                }
            } else if (data != null) {
                //success
                callback(data, null)
            }
        }
    }


    //for access to driver UI
    override fun isCheckoutDriver(): Boolean {
        return profileStorage.isCheckoutDriver()
    }

}