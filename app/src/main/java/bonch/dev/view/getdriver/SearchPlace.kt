package bonch.dev.view.getdriver

import android.location.Geocoder
import androidx.fragment.app.Fragment
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.util.*

class SearchPlace : Session.SearchListener {


    companion object {

        private var searchSession: Session? = null
        private var searchPlace: SearchPlace = SearchPlace()
        private var searchManager: SearchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        private val searchOptions = SearchOptions()

        fun pointGeocoder(point: Point) {

            searchSession = searchManager.submit(
                point,
                16,
                searchOptions,
                searchPlace
            )
        }


        fun stringGeocoder(query: String) {

            searchSession = searchManager.submit(
                query,
                Geometry.fromPoint(Point(59.0, 30.0)),
                searchOptions,
                searchPlace
            )
        }
    }


    override fun onSearchError(error: Error) {}


    override fun onSearchResponse(response: Response) {


        //println(response.collection.children.firstOrNull()?.obj?.metadataContainer?.getItem(ToponymObjectMetadata::class.java)?.address?.additionalInfo)

        for (i in 0..3) {
            //println(response.collection.children.firstOrNull()?.obj?.metadataContainer?.getItem(ToponymObjectMetadata::class.java)?.address?.components?.get(i)?.name)
        }

        //val address = response.collection.children[0].obj!!.name

        //fromAddress.setText(address)
    }
}

