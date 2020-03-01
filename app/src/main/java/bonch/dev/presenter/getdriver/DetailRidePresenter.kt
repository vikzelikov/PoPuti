package bonch.dev.presenter.getdriver

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import bonch.dev.Constant.Companion.ADD_BANK_CARD_VIEW
import bonch.dev.Constant.Companion.OFFER_PRICE_VIEW
import bonch.dev.Coordinator.Companion.openActivity
import bonch.dev.view.getdriver.DetailRideView
import bonch.dev.view.getdriver.Routing
import bonch.dev.view.getdriver.SearchPlace
import com.yandex.mapkit.geometry.Point

class DetailRidePresenter(val context: Context, val detailRideView: DetailRideView) {

    private val FROM = "FROM"
    private val FROM_URI = "FROM_URI"
    private val TO = "TO"
    private val TO_URI = "TO_URI"

    private var searchPlace: SearchPlace? = null
    private var routing: Routing? = null
    var fromPoint: Point? = null
    var toPoint: Point? = null

    fun clickOfferPriceBtn(fragment: Fragment) {
        openActivity(OFFER_PRICE_VIEW, context, fragment)
    }


    fun clickAddBankCardBtn(fragment: Fragment) {
        openActivity(ADD_BANK_CARD_VIEW, context, fragment)
    }


    fun receiveAddresses(bundle: Bundle) {
        val fromAddress = bundle.getString(FROM, null)
        val fromUri = bundle.getString(FROM_URI, null)
        val toAddress = bundle.getString(TO, null)
        val toUri = bundle.getString(TO_URI, null)

        if (fromAddress != null && toAddress != null) {
            detailRideView.setAddresses(fromAddress, toAddress)
        }


        //TODO delete 50 !!!!
        if (fromUri != null) {
            if (fromUri.length > 50) {
                //search local
                fromPoint = getPoint(fromUri)
            } else {
                //search network
                searchPlace!!.stringGeocoder(fromUri)
            }
            println(fromUri)
        }

        if (toUri != null) {
            if (toUri.length > 50) {
                //search local
                toPoint = getPoint(toUri)
            } else {
                //search network
                searchPlace!!.stringGeocoder(toUri)
            }
        }

        //TODO
        while (true) {
            if(fromPoint != null && toPoint != null){
                routing!!.submitRequest(fromPoint!!, toPoint!!)
                break
            }
            println("${fromPoint} ******* ${toPoint} ")
        }
    }


    private fun getPoint(uri: String): Point? {
        var coordinate: Point?
        val lat: Double
        val long: Double
        var str: List<String>

        try {
            str = uri.split("=")
            str = str[1].split("%")
            long = str[0].toDouble()
            lat = str[1].substring(2, str[1].length - 4).toDouble()

            coordinate = Point(lat, long)
        } catch (ex: IndexOutOfBoundsException) {
            coordinate = null
        }

        return coordinate
    }


    init {
        if (searchPlace == null) {
            searchPlace = SearchPlace(this)
        }

        if (routing == null) {
            routing = Routing(context, detailRideView)
        }
    }
}