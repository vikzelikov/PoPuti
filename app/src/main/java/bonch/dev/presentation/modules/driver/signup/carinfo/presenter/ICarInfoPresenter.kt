package bonch.dev.presentation.modules.driver.signup.carinfo.presenter

import androidx.fragment.app.Fragment

interface ICarInfoPresenter {

    fun instance(): CarInfoPresenter

    fun showSuggest(fragment: Fragment, isCarName: Boolean)

    fun startSetDocs()

}