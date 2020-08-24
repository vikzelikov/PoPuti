package bonch.dev.poputi.domain.interactor.common.profile

import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import java.io.File


interface IProfileInteractor {

    fun initRealm()

    fun localSaveProfile(profile: Profile)

    fun removeProfileData()

    fun remoteSaveProfile(profile: Profile)

    fun getProfileRemote(callback: DataHandler<Profile?>)

    fun loadPhoto(image: File, profile: Profile, callback: SuccessHandler)

    fun getProfileLocal(): Profile?

    fun getDriverAccess(): Boolean

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun saveBankCard(card: BankCard)

    fun getBankCards(): ArrayList<BankCard>

    fun deleteBankCard(card: BankCard)

    fun closeRealm()

}