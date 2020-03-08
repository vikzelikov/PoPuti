package bonch.dev.model.getdriver

import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.presenter.getdriver.GetDriverPresenter
import com.yandex.mapkit.geometry.Point

class GetDriverModel(
    private val getDriverPresenter: GetDriverPresenter
) {

    private var autocomplete: Autocomplete? = null
    private var geocoder: Geocoder? = null

    fun requestSuggest(query: String, userLocationPoint: Point?) {

        if (autocomplete == null) {
            autocomplete = Autocomplete(this, userLocationPoint)
        }

        autocomplete!!.requestSuggest(query)

    }


    fun responseSuggest(suggestResult: ArrayList<Ride>) {
        getDriverPresenter.setRecyclerSuggest(suggestResult)
    }


    fun requestGeocoder(point: Point) {
        if (geocoder == null) {
            geocoder = Geocoder(this)
        }

        geocoder!!.request(point)
    }


    fun responseGeocoder(address: String?, point: Point?){
        getDriverPresenter.responseGeocoder(address, point)
    }

}