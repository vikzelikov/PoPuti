package bonch.dev.presentation.presenter.passanger.getdriver

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.data.repository.passanger.getdriver.SearchPlace
import bonch.dev.data.repository.passanger.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.data.repository.passanger.getdriver.pojo.Coordinate.toAdr
import bonch.dev.data.repository.passanger.getdriver.pojo.PaymentCard
import bonch.dev.data.repository.passanger.getdriver.pojo.Ride
import bonch.dev.data.repository.passanger.getdriver.pojo.RideDetailInfo
import bonch.dev.presentation.presenter.passanger.getdriver.adapters.PaymentsListAdapter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.utils.Constants.AVERAGE_PRICE
import bonch.dev.utils.Constants.CARD_IMG
import bonch.dev.utils.Constants.FROM
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.OFFER_PRICE
import bonch.dev.utils.Constants.OFFER_PRICE_VIEW
import bonch.dev.utils.Constants.RIDE_DETAIL_INFO
import bonch.dev.utils.Constants.TO
import bonch.dev.utils.Coordinator.openActivity
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.utils.Keyboard.hideKeyboard
import bonch.dev.utils.Keyboard.showKeyboard
import bonch.dev.presentation.ui.passanger.getdriver.CreateRideView
import bonch.dev.presentation.ui.passanger.getdriver.DetailRideView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.create_ride_fragment.*

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


    fun receiveAddresses(fromAddress: Ride?, toAddress: Ride?) {
        val fromUri = fromAddress?.uri
        val toUri = toAddress?.uri

        detailRideView.setAddresses(fromAddress?.address!!, toAddress?.address!!)

        if (fromAddress.point != null) {
            fromPoint = Point(fromAddress.point!!.latitude, fromAddress.point!!.longitude)
        }

        if (toAddress.point != null) {
            toPoint = Point(toAddress.point!!.latitude, toAddress.point!!.longitude)
        }

        //50 - ok length for detect correct URI or no
        if (fromUri != null) {
            if (fromUri.length > 50) {
                fromPoint = getPoint(fromUri)
            } else {
                searchPlace!!.request(fromUri)
            }
        }

        if (toUri != null) {
            if (toUri.length > 50) {
                toPoint = getPoint(toUri)
            } else {
                searchPlace!!.request(toUri)
            }
        }

        submitRoute()
    }


    fun submitRoute() {
        if (fromPoint != null && toPoint != null) {
            routing!!.submitRequest(fromPoint!!, toPoint!!)
        }
    }


    fun removeRoute() {
        if (routing != null) {
            routing!!.removeRoute()
        }
    }


    //parse and get URI
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


    fun offerPriceDone(data: Intent?) {
        val offerPrice = getView().offer_price
        val priceLabelColor = getView().info_price
        val price = data!!.getStringExtra(OFFER_PRICE)
        val averagePrice = data.getIntExtra(AVERAGE_PRICE, 0)

        offerPrice.textSize = 22f
        offerPrice.setPadding(0, 5, 0, 25)
        offerPrice.setTextColor(Color.parseColor("#000000"))
        offerPrice.typeface = Typeface.DEFAULT_BOLD

        if (averagePrice <= (price!!.toInt())) {
            priceLabelColor.visibility = View.GONE
        } else {
            priceLabelColor.visibility = View.VISIBLE
        }

        offerPrice.text = price

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    fun addBankCardDone(data: Intent?) {
        val cardNumber = data?.getStringExtra(Constants.CARD_NUMBER)
        val validUntil = data?.getStringExtra(Constants.VALID_UNTIL)
        val cvc = data?.getStringExtra(Constants.CVC)
        val img = data?.getIntExtra(CARD_IMG, R.drawable.ic_visa)

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

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    fun getPaymentMethods() {
        detailRideView.cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun getInfoPrice() {
        detailRideView.infoPriceBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }


    fun commentStart() {
        val commentText = getView().comment_text
        val activity = getView().activity as MainActivity
        detailRideView.commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

        if (!commentText.isFocused) {
            commentText.requestFocus()
            //set a little timer to open keyboard
            Handler().postDelayed({
                showKeyboard(activity)
            }, 200)
        }
    }


    fun commentDone() {
        val commentText = getView().comment_text
        commentText?.clearFocus()

        hideAllBottomSheet()
    }


    fun hideAllBottomSheet(){
        val activity = getView().activity as MainActivity
        val root = getView().view!!

        hideKeyboard(activity, root)

        detailRideView.commentBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        detailRideView.cardsBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        detailRideView.infoPriceBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    fun showRoute() {
        routing?.showRoute()
    }


    fun onSlideBottomSheet(slideOffset: Float) {
        val onMapView = getView().on_map_view_detail
        val backBtn = getView().back_btn
        val showRoute = getView().show_route

        if (slideOffset > 0 && slideOffset < 1) {
            onMapView.alpha = slideOffset * 0.8f
            backBtn.alpha = 1 - slideOffset * 0.99f
            showRoute.alpha = 1 - slideOffset * 0.99f
        }
    }


    fun onStateChangedBottomSheet(newState: Int) {
        val commentText = getView().comment_text
        val onMapView = getView().on_map_view_detail
        val mainInfoLayout = getView().container_detail_ride

        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            val activity = getView().activity as MainActivity
            val root = getView().view!!
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


    fun getDriver() {
        if (isDataComplete()) {
            val bundle = Bundle()
            val fm = (getView().activity as MainActivity).supportFragmentManager
            val comment = getView().comment_min_text.text.toString().trim()
            val price = getView().offer_price.text.toString().trim()

            bundle.putParcelable(FROM, fromAdr)
            bundle.putParcelable(TO, toAdr)
            bundle.putParcelable(RIDE_DETAIL_INFO, RideDetailInfo(price, comment))

            replaceFragment(GET_DRIVER_VIEW, bundle, fm)
        }
    }


    private fun isDataComplete(): Boolean {
        val isComplete: Boolean
        val offerPrice = getView().offer_price.text.toString().trim()

        if (getView().selected_payment_method.visibility == View.VISIBLE && offerPrice.length < 5) {
            isComplete = true
            changeBtnEnable(true)
        } else {
            isComplete = false
            changeBtnEnable(false)
        }

        return isComplete
    }


    private fun changeBtnEnable(isEnable: Boolean) {
        val nextBtn = getView().get_driver_btn

        if (isEnable) {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    fun onBackPressed(): Boolean {
        val isBackPressed: Boolean
        val cardsBottomSheetBehavior = detailRideView.cardsBottomSheetBehavior
        val commentBottomSheetBehavior = detailRideView.commentBottomSheetBehavior

        if (cardsBottomSheetBehavior!!.state != BottomSheetBehavior.STATE_COLLAPSED
            || commentBottomSheetBehavior!!.state != BottomSheetBehavior.STATE_COLLAPSED) {
            //hide all bottom sheet
            hideAllBottomSheet()

            isBackPressed = false
        } else {
            val offerPrice = getView().offer_price
            getView().info_price.visibility = View.GONE
            offerPrice.text = getView().resources.getString(R.string.offer_price)
            offerPrice.textSize = 14f
            offerPrice.setPadding(0, 15, 0, 35)
            offerPrice.setTextColor(Color.parseColor("#50000000"))
            offerPrice.typeface = Typeface.DEFAULT

            isBackPressed = true
        }

        return isBackPressed
    }


    fun correctMapView() {
        Thread(Runnable {
            while (true) {
                val height = getView().container_detail_ride.height
                if (height > 0) {
                    val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    //"-10" for correct view radius corners
                    layoutParams.setMargins(0, 0, 0, height - 10)
                    getView().map.layoutParams = layoutParams
                    break
                }
            }
        }).start()
    }


    private fun getView(): CreateRideView {
        return detailRideView.getView()
    }
}