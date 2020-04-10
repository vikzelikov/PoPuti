package bonch.dev.model.passanger.getdriver

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error

class Geocoder(private val createRideModel: CreateRideModel) : Session.SearchListener {

    private var searchSession: Session? = null
    private var searchManager: SearchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val searchOptions = SearchOptions()

    fun request(point: Point) {

        searchSession = searchManager.submit(
            point,
            16,
            searchOptions,
            this
        )
    }


    override fun onSearchError(error: Error) {}


    override fun onSearchResponse(response: Response) {
        val point = response.collection.children[0].obj?.geometry?.first()?.point
        val address = response.collection.children[0].obj!!.name

        createRideModel.responseGeocoder(address, point)
    }
}

