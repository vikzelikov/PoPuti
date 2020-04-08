package bonch.dev.model.passanger.getdriver

import bonch.dev.model.passanger.getdriver.pojo.Ride
import bonch.dev.presenter.passanger.getdriver.CreateRidePresenter
import com.yandex.mapkit.geometry.Point

class CreateRideModel(
    private val createRidePresenter: CreateRidePresenter
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
        createRidePresenter.responseSuggest(suggestResult)
    }


    fun requestGeocoder(point: Point) {
        if (geocoder == null) {
            geocoder = Geocoder(this)
        }

        geocoder!!.request(point)
    }


    fun responseGeocoder(address: String?, point: Point?){
        createRidePresenter.responseGeocoder(address, point)
    }

}