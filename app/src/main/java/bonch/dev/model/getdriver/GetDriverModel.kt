package bonch.dev.model.getdriver

import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.GetDriverPresenter

class GetDriverModel(private val getDriverPresenter: GetDriverPresenter) {

    var i = 0

    fun getNewDriver() {
        val driver = if (i == 0) {
            Driver("Костя $i", "Hyundai Solaris", "DF456S", 4.3, R.drawable.ava, 344)
        } else {
            Driver("Александр $i", "Kia Rio", "AR432V", 3.9, R.drawable.ava1, 412)
        }

        if (i < 12)
            getDriverPresenter.setNewDriverOffer(driver)

        i++
    }
}