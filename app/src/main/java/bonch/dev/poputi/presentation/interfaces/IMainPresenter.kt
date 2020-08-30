package bonch.dev.poputi.presentation.interfaces

import bonch.dev.poputi.presentation.base.MainPresenter

interface IMainPresenter {

    fun instance(): MainPresenter

    fun onBackPressed()

    fun navigate()

    fun showPassengerView()

    fun showDriverView()

    fun updateFirebaseToken(firebaseToken: String)

}