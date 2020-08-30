package bonch.dev.poputi.domain.interactor

import bonch.dev.poputi.App
import bonch.dev.poputi.data.repository.common.ride.IRideRepository
import bonch.dev.poputi.data.repository.passenger.signup.ISignupRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.data.storage.passenger.getdriver.IGetDriverStorage
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import javax.inject.Inject

class BaseInteractor : IBaseInteractor {

    @Inject
    lateinit var signupRepository: ISignupRepository

    @Inject
    lateinit var rideRepository: IRideRepository

    @Inject
    lateinit var profileStorage: IProfileStorage

    @Inject
    lateinit var getDriverStorage: IGetDriverStorage


    init {
        App.appComponent.inject(this)
    }


    override fun validateAccount(callback: DataHandler<Profile?>) {
        val token = profileStorage.getToken()
        if (token != null) {
            signupRepository.getUserId(token, callback)
        } else callback(null, null)
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
        rideRepository.getRide(rideId) { data, error ->
            if (data == null && error != null) {
                //retry request
                rideRepository.getRide(rideId) { dataRide, _ ->
                    if (dataRide == null) {
                        //error
                        callback(null, "Error")
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


    override fun updateFirebaseToken(firebaseToken: String) {
        val token = profileStorage.getToken()

        if (token != null) {
            signupRepository.updateFirebaseToken(firebaseToken, token)
        }
    }
}