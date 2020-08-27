package bonch.dev.poputi.data.repository.driver.rating

import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.interfaces.DataHandler

interface IRatingRepository {

    fun getRating(token: String, callback: DataHandler<ArrayList<Review>?>)

}