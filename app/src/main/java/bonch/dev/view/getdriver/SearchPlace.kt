package bonch.dev.view.getdriver

import bonch.dev.presenter.getdriver.DetailRidePresenter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error

class SearchPlace(val detailRidePresenter: DetailRidePresenter) : Session.SearchListener {

    private var searchSession: Session? = null
    private var searchManager: SearchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val searchOptions = SearchOptions()

    fun pointGeocoder(point: Point) {

        searchSession = searchManager.submit(
            point,
            16,
            searchOptions,
            this
        )
    }


    fun stringGeocoder(uri: String) {
        searchSession = searchManager.resolveURI(uri, searchOptions, this)
    }


    override fun onSearchError(error: Error) {}


    override fun onSearchResponse(response: Response) {

        val point = response.collection.children[0].obj?.geometry?.first()?.point
        if (detailRidePresenter.fromPoint == null) {
            detailRidePresenter.fromPoint = point
        } else {
            detailRidePresenter.toPoint = point
        }

        //println(response.collection.children.firstOrNull()?.obj?.metadataContainer?.getItem(ToponymObjectMetadata::class.java)?.address?.additionalInfo)

        for (i in 0..3) {
            //println(response.collection.children.firstOrNull()?.obj?.metadataContainer?.getItem(ToponymObjectMetadata::class.java)?.address?.components?.get(i)?.name)
        }

        //val address = response.collection.children[0].obj!!.name

        //fromAddress.setText(address)
    }
}

