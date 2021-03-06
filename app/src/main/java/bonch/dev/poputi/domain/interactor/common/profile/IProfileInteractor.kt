package bonch.dev.poputi.domain.interactor.common.profile

import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import java.io.File


interface IProfileInteractor {

    fun initRealm()

    fun removeProfileData()

    fun saveProfile(profile: Profile)

    fun getProfile(callback: DataHandler<Profile?>)

    fun uploadPhoto(image: File, isAva: Boolean, callback: SuccessHandler)

    fun deletePhoto(imageId: Int, callback: SuccessHandler)

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun saveBankCard(card: BankCard)

    fun getBankCards(): ArrayList<BankCard>

    fun deleteBankCard(card: BankCard)

    fun closeRealm()


    //STORY RIDES
    fun getStoryRidesPassenger(callback: DataHandler<ArrayList<RideInfo>?>)

    fun getStoryRidesDriver(callback: DataHandler<ArrayList<RideInfo>?>)


    //RATING
    fun getRating(callback: DataHandler<ArrayList<Review>?>)


    //SELECT CITY
    fun saveMyCity(address: Address)

    fun getMyCity(): Address?


    //ONBOARDING
    fun saveOnboardingPassenger()

    fun getOnboardingPassenger(): Boolean

    fun saveOnboardingDriver()

    fun getOnboardingDriver(): Boolean

}