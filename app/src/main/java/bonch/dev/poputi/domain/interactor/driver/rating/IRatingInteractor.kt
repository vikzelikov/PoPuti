package bonch.dev.poputi.domain.interactor.driver.rating

import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.interfaces.DataHandler

interface IRatingInteractor {

    fun getProfile(callback: DataHandler<Profile?>)

    fun getRating(callback: DataHandler<ArrayList<Review>?>)

}