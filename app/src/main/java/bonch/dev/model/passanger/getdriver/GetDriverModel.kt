package bonch.dev.model.passanger.getdriver

import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.Driver
import bonch.dev.presenter.passanger.getdriver.GetDriverPresenter

class GetDriverModel(private val getDriverPresenter: GetDriverPresenter) {

    var i = 0

    fun getNewDriver() {
        val driver = if (i % 2 == 0) {
            Driver("Костя $i", "Hyundai Solaris", "DF456S", 4.3, R.drawable.ava, 344 + i * 9)
        } else {
            Driver("Александр $i", "Kia Rio", "AR432V", 3.9, R.drawable.ava1, 412 + i * 5)
        }

        if (i < 20)
            getDriverPresenter.setNewDriverOffer(driver)

        i++
    }
}