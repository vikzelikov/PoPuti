package bonch.dev.poputi.presentation.modules.common.profile.menu.presenter

import android.content.Intent
import androidx.fragment.app.Fragment


interface IProfilePresenter {

    fun instance(): ProfilePresenter

    fun getProfile()

    fun showFullProfile(fragment: Fragment)

    fun checkoutAccount(isPassanger: Boolean, fragment: Fragment)

    fun profileDataResult(data: Intent)

    fun addBankCard()

    fun confirmPerson(fragment: Fragment)

    fun storyOrders()

    fun logout()

    fun onDestroy()

}