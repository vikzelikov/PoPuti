package bonch.dev.poputi.data.repository.common.ride

import bonch.dev.poputi.presentation.interfaces.DataHandler
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error

class SearchPlace : Session.SearchListener {

    private var searchSession: Session? = null
    private var searchManager: SearchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val searchOptions = SearchOptions()
    private var callback: DataHandler<Point?>? = null


    fun request(uri: String, callback: DataHandler<Point?>) {
        this.callback = callback

        searchSession = searchManager.resolveURI(uri, searchOptions, this)
    }


    override fun onSearchError(error: Error) {}


    override fun onSearchResponse(response: Response) {
        try {
            val point = response.collection.children.first().obj?.geometry?.first()?.point

            callback?.let { it(point, null) }

        } catch (ex: NoSuchElementException) {
        }
    }
}

