package bonch.dev.data.repository.common.rate

import bonch.dev.domain.entities.common.rate.Review
import bonch.dev.presentation.interfaces.SuccessHandler

interface IRateRideRepository {

    fun sendReview(
        review: Review,
        token: String,
        callback: SuccessHandler
    )

}