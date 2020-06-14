package bonch.dev.data.repository.common.ride

import bonch.dev.domain.entities.common.ride.Address
import bonch.dev.domain.interactor.passenger.getdriver.SuggestHandler
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import java.util.*
import kotlin.math.min

class Autocomplete(userLocationPoint: Point?) : SuggestSession.SuggestListener {

    private val MAX_COUNT_SUGGEST = 7

    private val searchManager: SearchManager = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.COMBINED
    )

    private val suggestResult: ArrayList<Address> = arrayListOf()
    lateinit var callback: SuggestHandler

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


    fun requestSuggest(query: String, callback: SuggestHandler) {
        this.callback = callback
        val box = BOUNDING_BOX

        if (box != null) {
            val suggestSession: SuggestSession = searchManager.createSuggestSession()
            suggestSession.suggest(query, box, SUGGEST_OPTIONS, this)
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