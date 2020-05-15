package bonch.dev.presentation.modules.driver.signup.carinfo.view

import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.presentation.interfaces.IBaseView

interface ICarInfoView : IBaseView {

    fun changeBtnEnable(enable: Boolean)

    fun getData(): DriverData

}