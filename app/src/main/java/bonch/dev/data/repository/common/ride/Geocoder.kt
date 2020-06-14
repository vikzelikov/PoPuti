package bonch.dev.data.repository.common.ride

import bonch.dev.domain.interactor.passenger.getdriver.GeocoderHandler
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.util.*
import kotlin.NoSuchElementException


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
            var address = response.collection.children.first().obj?.name

            try {
                address = address?.substring(0, 1)?.toUpperCase(Locale("RU")) +
                        address?.substring(1)?.toLowerCase(Locale("RU"))
            } catch (ex: StringIndexOutOfBoundsException) {
            }

            callback(address, this.point)
        } catch (ex: NoSuchElementException) {
        }


    }
}

