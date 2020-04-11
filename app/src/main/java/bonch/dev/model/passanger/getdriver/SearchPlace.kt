package bonch.dev.model.passanger.getdriver

import bonch.dev.presenter.passanger.getdriver.DetailRidePresenter
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error

class SearchPlace(val detailRidePresenter: DetailRidePresenter) : Session.SearchListener {

    private var searchSession: Session? = null
    private var searchManager: SearchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val searchOptions = SearchOptions()


    fun request(uri: String) {
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

        detailRidePresenter.submitRoute()
    }
}
