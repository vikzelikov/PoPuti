package bonch.dev.poputi.presentation.interfaces

import android.content.Intent
import bonch.dev.poputi.presentation.base.MainPresenter

interface IMainPresenter {

    fun instance(): MainPresenter

    fun onBackPressed()

    fun navigate(intent: Intent?)

    fun showPassengerView()

    fun showDriverView()

    fun updateFirebaseToken(firebaseToken: String)

}