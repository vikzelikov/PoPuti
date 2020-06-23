package bonch.dev.domain.interactor.common.rate

import bonch.dev.data.repository.common.rate.IRateRideRepository
import bonch.dev.data.storage.common.profile.IProfileStorage
import bonch.dev.domain.entities.common.rate.Review
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.presentation.interfaces.SuccessHandler
import bonch.dev.presentation.modules.common.CommonComponent
import javax.inject.Inject

class RateRideInteractor : IRateRideInteractor {

    @Inject
    lateinit var rateRideRepository: IRateRideRepository

    @Inject
    lateinit var profileStorage: IProfileStorage

    init {
        CommonComponent.commonComponent?.inject(this)
    }


    override fun sendReview(review: Review, byPassenger: Boolean, callback: SuccessHandler) {
        val token = profileStorage.getToken()
        val fromId = profileStorage.getUserId()
        val toId = if (byPassenger) ActiveRide.activeRide?.driver?.id
        else ActiveRide.activeRide?.passenger?.id

        if (token != null && fromId != -1 && toId != null) {
            review.fromId = fromId
            review.toId = toId

            rateRideRepository.sendReview(review, token) { isSuccess ->
                if (isSuccess) callback(true)
                else rateRideRepository.sendReview(review, token, callback)
            }
        } else callback(false)
    }
}