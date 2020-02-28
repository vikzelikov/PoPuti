package bonch.dev.model.getdriver

import bonch.dev.model.getdriver.pojo.Ride
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error

class Autocomplete(var getDriverModel: GetDriverModel, userLocationPoint: Point) :
    SuggestSession.SuggestListener {

    private val RESULT_NUMBER_LIMIT = 7

    private val searchManager: SearchManager = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.COMBINED
    )

    private val suggestResult: ArrayList<Ride> = arrayListOf()

    private val CENTER = userLocationPoint

    private val BOUNDING_BOX = BoundingBox(
        Point(CENTER.latitude, CENTER.longitude),
        Point(CENTER.latitude, CENTER.longitude)
    )

    private val SUGGEST_OPTIONS = SuggestOptions(
        SuggestType.GEO.value or SuggestType.BIZ.value or SuggestType.TRANSIT.value,
        userLocationPoint,
        false
    )


    override fun onError(error: Error) {}


    override fun onResponse(suggest: MutableList<SuggestItem>) {
//TODO
        suggestResult.clear()

        for (i in 0 until Math.min(RESULT_NUMBER_LIMIT, suggest.size)) {
            if (suggest[i].subtitle != null) {

                if(suggest[i].uri.toString().length > 50){

                    suggestResult.add(
                        Ride(
                            suggest[i].title.text,
                            suggest[i].subtitle!!.text
                        )
                    )

                    if (suggest[i].uri != null) {
                        println(suggest[i].uri)
                        println(getPoint(suggest[i].uri!!).latitude)
                        println(getPoint(suggest[i].uri!!).longitude)
                    }
                }


            } else {
                suggestResult.add(
                    Ride(
                        suggest[i].displayText!!,
                        "Город"
                    )
                )
            }
        }


        getDriverModel.responseSuggest(suggestResult)

    }


    fun requestSuggest(query: String) {
        val suggestSession: SuggestSession

        suggestSession = searchManager.createSuggestSession()
        suggestSession.suggest(query, BOUNDING_BOX, SUGGEST_OPTIONS, this)
    }


    private fun getPoint(uri: String): Point {
        val coordinate: Point
        val lat: Double
        val long: Double
        var str: List<String>

        str = uri.split("=")
        str = str[1].split("%")
        long = str[0].toDouble()
        lat = str[1].substring(2, str[1].length - 4).toDouble()

        coordinate = Point(lat, long)

        return coordinate
    }
}