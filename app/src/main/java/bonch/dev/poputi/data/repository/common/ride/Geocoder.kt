package bonch.dev.poputi.data.repository.common.ride

import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.AddressPoint
import bonch.dev.poputi.presentation.interfaces.GeocoderHandler
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.util.*


class Geocoder : Session.SearchListener {

    lateinit var callback: GeocoderHandler
    lateinit var point: Point
    private var searchSession: Session? = null
    private var searchManager: SearchManager? = null

    private val searchOptions = SearchOptions()

    fun request(point: Point, callback: GeocoderHandler) {
        if (searchManager == null) {
            searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        }

        this.point = point
        this.callback = callback

        searchSession = searchManager?.submit(
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

            val city = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(com.yandex.mapkit.search.Address.Component.Kind.LOCALITY) }
                ?.name

            val addr = Address()
            addr.address = address
            addr.point = AddressPoint(point.latitude, point.longitude)
            addr.description = city

            callback(addr)
        } catch (ex: NoSuchElementException) {
        }


    }
}

