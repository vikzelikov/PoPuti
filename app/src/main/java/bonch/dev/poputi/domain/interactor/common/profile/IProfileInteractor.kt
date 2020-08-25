package bonch.dev.poputi.domain.interactor.common.profile

import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.profile.verification.NewPhoto
import bonch.dev.poputi.domain.entities.common.profile.verification.Verify
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import java.io.File


interface IProfileInteractor {

    fun initRealm()

    fun removeProfileData()

    fun saveProfile(profile: Profile)

    fun getProfile(callback: DataHandler<Profile?>)

    fun uploadPhoto(image: File, profile: Profile, callback: SuccessHandler)

    fun getDriverAccess(): Boolean

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun saveBankCard(card: BankCard)

    fun getBankCards(): ArrayList<BankCard>

    fun deleteBankCard(card: BankCard)

    fun closeRealm()


    //VERIFICATION
    fun verification(data: Verify, callback: SuccessHandler)

    fun uploadPhoto(image: File, id: Int, callback: SuccessHandler)

    fun deletePhoto(imageId: Int, callback: SuccessHandler)

    fun putNewPhoto(photo: NewPhoto, callback: SuccessHandler)

}