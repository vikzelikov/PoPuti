package bonch.dev.poputi.domain.interactor.driver.rating

import bonch.dev.poputi.data.repository.common.profile.IProfileRepository
import bonch.dev.poputi.data.repository.driver.rating.IRatingRepository
import bonch.dev.poputi.data.storage.common.profile.IProfileStorage
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.modules.driver.rating.RatingComponent
import javax.inject.Inject

class RatingInteractor : IRatingInteractor {

    @Inject
    lateinit var ratingRepository: IRatingRepository


    @Inject
    lateinit var profileRepository: IProfileRepository


    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        RatingComponent.ratingComponent?.inject(this)
    }


    override fun getProfile(callback: DataHandler<Profile?>) {
        val token = profileStorage.getToken()
        val userId = profileStorage.getUserId()

        if (token != null && userId != -1) {
            profileRepository.getProfile(userId, token, callback)
        } else {
            //error
            callback(null, "error")
        }
    }


    override fun getRating(callback: DataHandler<ArrayList<Review>?>) {
        val token = profileStorage.getToken()

        if (token != null) {
            ratingRepository.getRating(token, callback)

        } else callback(null, "error")
    }

}