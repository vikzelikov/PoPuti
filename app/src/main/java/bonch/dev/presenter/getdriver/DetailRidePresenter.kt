package bonch.dev.presenter.getdriver

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import bonch.dev.Constant.Companion.ADD_BANK_CARD_VIEW
import bonch.dev.Constant.Companion.OFFER_PRICE_VIEW
import bonch.dev.Coordinator.Companion.openActivity
import bonch.dev.model.getdriver.SearchPlace
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.view.getdriver.DetailRideView
import com.yandex.mapkit.geometry.Point

class DetailRidePresenter(val context: Context, val detailRideView: DetailRideView) {

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


    fun receiveAddresses(fromAddress: Ride, toAddress: Ride) {
        val fromUri = fromAddress.uri
        val toUri = toAddress.uri

        detailRideView.setAddresses(fromAddress.address!!, toAddress.address!!)

        if (fromAddress.point != null) {
            fromPoint = fromAddress.point
        }

        if (toAddress.point != null) {
            toPoint = toAddress.point
        }

        if (fromUri != null) {
            if (fromUri.length > 50) {
                fromPoint = getPoint(fromUri)
            } else {
                searchPlace!!.request(fromUri)
            }
        }

        if (toUri != null) {
            //TODO
            if (toUri.length > 50) {
                toPoint = getPoint(toUri)
            } else {
                searchPlace!!.request(toUri)
            }
        }

        submitRouting()
    }


    fun submitRouting() {
        if (fromPoint != null && toPoint != null) {
            routing!!.submitRequest(fromPoint!!, toPoint!!)
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


    fun removeRoute(){
        if(routing != null){
            routing!!.removeRoute()
        }
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