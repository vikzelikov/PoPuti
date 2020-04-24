package bonch.dev.data.repository.passanger.getdriver

import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import bonch.dev.domain.utils.Constants
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class Autocomplete(private var createRideModel: CreateRideModel, userLocationPoint: Point?) :
    SuggestSession.SuggestListener {

    private val searchManager: SearchManager = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.COMBINED
    )

    private val suggestResult: ArrayList<Ride> = arrayListOf()

    private var BOUNDING_BOX = userLocationPoint?.let {
        BoundingBox(
            Point(it.latitude, it.longitude),
            Point(it.latitude, it.longitude)
        )
    }

    private val SUGGEST_OPTIONS = SuggestOptions(
        SuggestType.GEO.value or SuggestType.BIZ.value or SuggestType.TRANSIT.value,
        userLocationPoint,
        false
    )


    override fun onError(error: Error) {}


    override fun onResponse(suggest: MutableList<SuggestItem>) {
        suggestResult.clear()

        for (i in 0 until min(Constants.MAX_COUNT_SUGGEST, suggest.size)) {
            if (suggest[i].subtitle != null) {
                if (suggest[i].uri != null) {
                    //get id for Realm-db
                    val id = suggest[i].uri.hashCode()
                    //uppercase first letter
                    val address = suggest[i].title.text.substring(0, 1).toUpperCase(Locale("RU")) +
                            suggest[i].title.text.substring(1).toLowerCase(Locale("RU"))

                    suggestResult.add(
                        Ride(
                            id,
                            address,
                            suggest[i].subtitle?.text,
                            suggest[i].uri
                        )
                    )
                }

            }
        }

        createRideModel.responseSuggest(suggestResult)

    }


    fun requestSuggest(query: String) {
        if (BOUNDING_BOX == null) {
            BOUNDING_BOX = BoundingBox()
        }

        val suggestSession: SuggestSession = searchManager.createSuggestSession()
        suggestSession.suggest(query, BOUNDING_BOX!!, SUGGEST_OPTIONS, this)
    }
}