package bonch.dev.poputi.presentation.modules.common.profile.menu.presenter

import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.common.ride.Address


interface IProfilePresenter {

    fun instance(): ProfilePresenter

    fun getProfile()

    fun showFullProfile(fragment: Fragment)

    fun checkoutAccount(isPassanger: Boolean, fragment: Fragment)

    fun profileDataResult(data: Intent)

    fun addBankCard()

    fun confirmPerson(fragment: Fragment)

    fun showSelectCity(fragment: Fragment)

    fun changeLang(fragment: Fragment)

    fun showProfits()

    fun showSupport(fragment: Fragment)

    fun showRating()

    fun showCarInfo()

    fun showStoryOrders(isPassanger: Boolean)

    fun getMyCity(): Address?

    fun checkModerateVerification(photos: Array<Photo>)

    fun checkOnboarding(isForPassenger: Boolean)

    fun logout()

    fun onDestroy()

}