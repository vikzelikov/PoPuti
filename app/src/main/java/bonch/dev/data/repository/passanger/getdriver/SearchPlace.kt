package bonch.dev.data.repository.passanger.getdriver

import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.DetailRidePresenter
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.lang.IndexOutOfBoundsException

class SearchPlace(private val detailRidePresenter: DetailRidePresenter) : Session.SearchListener {

    private var searchSession: Session? = null
    private var searchManager: SearchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val searchOptions = SearchOptions()


    fun request(uri: String) {
        searchSession = searchManager.resolveURI(uri, searchOptions, this)
    }


    override fun onSearchError(error: Error) {}


    override fun onSearchResponse(response: Response) {
        try {
            val point = response.collection.children.first().obj?.geometry?.first()?.point

            if (detailRidePresenter.fromPoint == null) {
                detailRidePresenter.fromPoint = point
            } else {
                detailRidePresenter.toPoint = point
            }
        }catch (ex: NoSuchElementException){}


        detailRidePresenter.submitRoute()
    }
}

