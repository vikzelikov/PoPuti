package bonch.dev.data.repository.passanger.getdriver

import com.yandex.mapkit.geometry.Point

class GetDriverRepository : IGetDriverRepository {

    private var autocomplete: Autocomplete? = null
    private var geocoder: Geocoder? = null


    init {
        geocoder = Geocoder()
    }


    //Geocoder
    override fun requestGeocoder(point: Point, callback: GeocoderHandler) {
        geocoder?.request(point) { address, responsePoint ->
            callback(address, responsePoint)
        }
    }


    //Suggest (autocomplete)
    override fun requestSuggest(query: String, userLocationPoint: Point?) {
        if (autocomplete == null) {
            autocomplete = Autocomplete(userLocationPoint)
        }

        autocomplete!!.requestSuggest(query)
    }

}