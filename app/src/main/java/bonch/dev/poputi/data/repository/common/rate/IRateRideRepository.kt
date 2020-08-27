package bonch.dev.poputi.data.repository.common.rate

import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler

interface IRateRideRepository {

    fun sendReview(
        review: Review,
        token: String,
        callback: SuccessHandler
    )

    fun getRating(token: String, callback: DataHandler<ArrayList<Review>?>)


}