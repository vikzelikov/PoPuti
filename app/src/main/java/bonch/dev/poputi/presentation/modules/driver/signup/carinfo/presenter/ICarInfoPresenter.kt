package bonch.dev.poputi.presentation.modules.driver.signup.carinfo.presenter

import androidx.fragment.app.Fragment
import bonch.dev.poputi.presentation.modules.driver.signup.carinfo.presenter.CarInfoPresenter

interface ICarInfoPresenter {

    fun instance(): CarInfoPresenter

    fun showSuggest(fragment: Fragment, isCarName: Boolean)

    fun startSetDocs()

}