package bonch.dev.poputi.data.repository.common.ride

import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.presentation.interfaces.SuggestHandler
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.util.*
import kotlin.math.min

class Autocomplete : SuggestSession.SuggestListener {

    private val MAX_COUNT_SUGGEST = 7

    private val searchManager: SearchManager = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.COMBINED
    )

    private val suggestResult: ArrayList<Address> = arrayListOf()
    lateinit var callback: SuggestHandler

    private var BOUNDING_BOX: BoundingBox? = null

    private var SUGGEST_OPTIONS: SuggestOptions? = null


    override fun onError(error: Error) {}


    fun requestSuggest(query: String, userPoint: Point?, callback: SuggestHandler) {
        userPoint?.let {
            if (BOUNDING_BOX == null)
                BOUNDING_BOX = BoundingBox(
                    Point(userPoint.latitude, userPoint.longitude),
                    Point(userPoint.latitude, userPoint.longitude)
                )

            if (SUGGEST_OPTIONS == null)
                SUGGEST_OPTIONS = SuggestOptions(
                    SuggestType.GEO.value or SuggestType.BIZ.value or SuggestType.TRANSIT.value,
                    userPoint,
                    false
                )

            this.callback = callback
            val box = BOUNDING_BOX
            val options = SUGGEST_OPTIONS

            if (box != null && options != null) {
                val suggestSession: SuggestSession = searchManager.createSuggestSession()
                suggestSession.suggest(query, box, options, this)
            }
        }
    }


    override fun onResponse(suggest: MutableList<SuggestItem>) {
        suggestResult.clear()

        for (i in 0 until min(MAX_COUNT_SUGGEST, suggest.size)) {
            if (suggest[i].uri != null) {
                //get id for Realm-db
                val id = suggest[i].uri.hashCode()
                //uppercase first letter
                try {
                    val address = suggest[i].title.text.substring(0, 1).toUpperCase(Locale("RU")) +
                            suggest[i].title.text.substring(1).toLowerCase(Locale("RU"))

                    suggestResult.add(
                        Address(
                            id,
                            address,
                            suggest[i].subtitle?.text,
                            suggest[i].uri
                        )
                    )
                } catch (ex: StringIndexOutOfBoundsException) {
                }
            }

        }
        //return suggest data
        callback(suggestResult)

    }
}