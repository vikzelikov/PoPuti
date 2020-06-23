package bonch.dev.domain.interactor.common.rate

import bonch.dev.domain.entities.common.rate.Review
import bonch.dev.presentation.interfaces.SuccessHandler

interface IRateRideInteractor {

    fun sendReview(
        review: Review,
        byPassenger: Boolean,
        callback: SuccessHandler
    )

}