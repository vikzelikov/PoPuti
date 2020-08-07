package bonch.dev.poputi.domain.interactor.common.rate

import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface IRateRideInteractor {

    fun sendReview(
        review: Review,
        byPassenger: Boolean,
        callback: SuccessHandler
    )

}