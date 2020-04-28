package bonch.dev.data.repository.passanger.getdriver

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.lang.IndexOutOfBoundsException


class Geocoder : Session.SearchListener {


    lateinit var callback: GeocoderHandler
    lateinit var point: Point
    private var searchSession: Session? = null
    private var searchManager: SearchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val searchOptions = SearchOptions()

    fun request(point: Point, callback: GeocoderHandler) {
        this.point = point
        this.callback = callback

        searchSession = searchManager.submit(
            point,
            16,
            searchOptions,
            this
        )
    }


    override fun onSearchError(error: Error) {}


    override fun onSearchResponse(response: Response) {
        try {
            val address = response.collection.children[0].obj?.name
            if (address != null)
                callback(address, this.point)
        } catch (ex: IndexOutOfBoundsException) {
        }
    }
}

