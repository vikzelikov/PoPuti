package bonch.dev.poputi.presentation.modules.driver.signup.carinfo.view

import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import bonch.dev.poputi.presentation.interfaces.IBaseView

interface ICarInfoView : IBaseView {

    fun changeBtnEnable(enable: Boolean)

    fun getData(): DriverData

}