package bonch.dev.presentation.interfaces

import bonch.dev.presentation.base.MainPresenter

interface IMainPresenter {

    fun instance(): MainPresenter

    fun getToken(): String?

    fun onBackPressed()

}