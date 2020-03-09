package bonch.dev.presenter.getdriver

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.MainActivity.Companion.showKeyboard
import bonch.dev.R
import bonch.dev.model.getdriver.SearchPlace
import bonch.dev.model.getdriver.pojo.PaymentCard
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.presenter.getdriver.adapters.AddressesListAdapter
import bonch.dev.presenter.getdriver.adapters.PaymentsListAdapter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.utils.Constants.OFFER_PRICE_VIEW
import bonch.dev.utils.Coordinator.openActivity
import bonch.dev.view.getdriver.DetailRideView
import bonch.dev.view.getdriver.GetDriverView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.detail_ride_layout.*
import kotlinx.android.synthetic.main.get_driver_fragment.*

class DetailRidePresenter(private val detailRideView: DetailRideView) {

    var paymentsListAdapter: PaymentsListAdapter? = null
    private var searchPlace: SearchPlace? = null
    private var routing: Routing? = null
    var fromPoint: Point? = null
    var toPoint: Point? = null


    init {
        if (searchPlace == null) {
            searchPlace = SearchPlace(this)
        }

        if (routing == null) {
            val context = detailRideView.getView().context!!
            routing = Routing(context, detailRideView)
        }
    }


    fun offerPrice(fragment: Fragment) {
        val activity = detailRideView.getView().activity!!
        openActivity(OFFER_PRICE_VIEW, activity, fragment)
    }


    fun addBankCard(fragment: Fragment) {
        val activity = detailRideView.getView().activity!!
        openActivity(ADD_BANK_CARD_VIEW, activity, fragment)
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


    fun removeRoute() {
        if (routing != null) {
            routing!!.removeRoute()
        }
    }


    fun offerPriceDone(context: Context, data: Intent?) {
        val offerPrice = getView().offer_price
        val priceLabelColor = getView().price_label_color
        val strPrice = data!!.getStringExtra(Constants.OFFER_PRICE)

        offerPrice.textSize = 22f
        offerPrice.setTextColor(Color.parseColor("#000000"))
        offerPrice.typeface = Typeface.DEFAULT_BOLD

        if (isPositiveOfferPrice(strPrice!!.toInt())) {
            priceLabelColor.text = getView().getString(R.string.positive)
            priceLabelColor.background =
                ContextCompat.getDrawable(context, R.drawable.bg_offer_price_positive)
        } else {
            priceLabelColor.text = getView().getString(R.string.negative)
            priceLabelColor.background =
                ContextCompat.getDrawable(context, R.drawable.bg_offer_price_negative)
        }

        priceLabelColor.visibility = View.VISIBLE
        offerPrice.text = strPrice
    }


    fun addBankCardDone(data: Intent?) {
        val cardNumber = data?.getStringExtra(Constants.CARD_NUMBER)
        val validUntil = data?.getStringExtra(Constants.VALID_UNTIL)
        val cvc = data?.getStringExtra(Constants.CVC)
        val img = data?.getIntExtra(Constants.BANK_IMG, R.drawable.ic_visa)

        val paymentCard = PaymentCard(cardNumber, validUntil, cvc, img)

        paymentsListAdapter?.list?.add(paymentCard)
        paymentsListAdapter?.notifyDataSetChanged()
    }


    fun setSelectedBankCard(paymentCard: PaymentCard) {
        getView().selected_payment_method.visibility = View.VISIBLE
        getView().payment_method.visibility = View.GONE

        getView().number_card.text = paymentCard.numberCard
        if (paymentCard.img != null) {
            getView().payment_method_img.setImageResource(paymentCard.img!!)
        }

        detailRideView.cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun getPaymentMethods() {
        detailRideView.cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun commentStart(activity: FragmentActivity) {
        val commentText = getView().comment_text
        detailRideView.commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

        if (!commentText.isFocused) {
            commentText.requestFocus()
            showKeyboard(activity)
        }
    }


    fun commentDone(activity: FragmentActivity, root: View) {
        val commentText = getView().comment_text

        commentText?.clearFocus()
        hideKeyboard(activity, root)
        detailRideView.commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        detailRideView.cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun onSlideBottomSheet(slideOffset: Float){
        val onMapView = getView().on_map_view_detail

        if (slideOffset > 0) {
            onMapView.alpha = slideOffset * 0.8f
        }
    }


    fun onStateChangedBottomSheet(newState: Int, activity: FragmentActivity, root: View){
        val commentText = getView().comment_text
        val onMapView = getView().on_map_view_detail
        val mainInfoLayout = getView().main_info_layout

        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard(activity, root)
            commentText.clearFocus()
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            onMapView.visibility = View.GONE
            mainInfoLayout.elevation = 30f
        } else {
            onMapView.visibility = View.VISIBLE
            mainInfoLayout.elevation = 0f
        }
    }



    private fun isPositiveOfferPrice(price: Int): Boolean {
        var isPositive = false
        //TODO

        if (price > 300) {
            isPositive = true
        }

        return isPositive
    }


    private fun getView(): GetDriverView {
        return detailRideView.getView()
    }
}