package bonch.dev.poputi.data.storage.common.profile

import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.Address

interface IProfileStorage {

    fun initRealm()

    fun removeProfile()

    fun saveToken(token: String)

    fun getToken(): String?

    fun saveUserId(id: Int)

    fun getUserId(): Int

    fun saveDriverId(id: Int)

    fun getDriverId(): Int

    fun saveCheckoutDriver(isDriver: Boolean)

    fun isCheckoutDriver(): Boolean

    fun saveMyCity(address: Address)

    fun getMyCity(): Address?

    fun saveBankCard(card: BankCard)

    fun getBankCards(): ArrayList<BankCard>

    fun deleteBankCard(card: BankCard)

    fun saveOnboardingPassenger()

    fun getOnboardingPassenger(): Boolean

    fun saveOnboardingDriver()

    fun getOnboardingDriver(): Boolean

    fun closeRealm()

}