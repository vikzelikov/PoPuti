package bonch.dev.model.getdriver

import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.presenter.getdriver.GetDriverPresenter
import com.yandex.mapkit.geometry.Point

class GetDriverModel(
    private val getDriverPresenter: GetDriverPresenter
) {

    private var autocomplete: Autocomplete? = null

    fun requestSuggest(query: String, userLocationPoint: Point) {

        if (autocomplete == null) {
            autocomplete = Autocomplete(this, userLocationPoint)
        }

        autocomplete!!.requestSuggest(query)

    }


    fun responseSuggest(suggestResult: ArrayList<Ride>) {
        getDriverPresenter.setRecyclerSuggest(suggestResult)
    }

}