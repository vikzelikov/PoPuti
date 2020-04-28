package bonch.dev.data.repository.passanger.getdriver

import com.yandex.mapkit.geometry.Point

typealias GeocoderHandler = (address: String, point: Point) -> Unit

interface IGetDriverRepository {

    fun requestGeocoder(point: Point, callback: GeocoderHandler)

    fun requestSuggest(query: String, userLocationPoint: Point?)

}