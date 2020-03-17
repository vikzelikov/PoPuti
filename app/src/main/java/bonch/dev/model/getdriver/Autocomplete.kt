package bonch.dev.model.getdriver

import bonch.dev.model.getdriver.pojo.Ride
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import kotlin.math.min

class Autocomplete(private var createRideModel: CreateRideModel, userLocationPoint: Point?) :
    SuggestSession.SuggestListener {

    private val RESULT_NUMBER_LIMIT = 7

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

        for (i in 0 until min(RESULT_NUMBER_LIMIT, suggest.size)) {
            if (suggest[i].subtitle != null) {

                //TODO check nullable uri
                if (suggest[i].uri != null) {
                    suggestResult.add(
                        Ride(
                            suggest[i].title.text,
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
        val suggestSession: SuggestSession

        if(BOUNDING_BOX == null){
            BOUNDING_BOX = BoundingBox()
        }

        suggestSession = searchManager.createSuggestSession()
        suggestSession.suggest(query, BOUNDING_BOX!!, SUGGEST_OPTIONS, this)
    }
}