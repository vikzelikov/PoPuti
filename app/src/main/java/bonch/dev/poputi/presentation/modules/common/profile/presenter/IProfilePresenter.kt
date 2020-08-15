package bonch.dev.poputi.presentation.modules.common.profile.presenter

import android.content.Intent
import androidx.fragment.app.Fragment


interface IProfilePresenter {

    fun instance(): ProfilePresenter

    fun getProfile()

    fun showFullProfile(fragment: Fragment)

    fun checkoutAccount(isPassanger: Boolean, fragment: Fragment)

    fun profileDataResult(data: Intent)

    fun logout()

    fun onDestroy()

}