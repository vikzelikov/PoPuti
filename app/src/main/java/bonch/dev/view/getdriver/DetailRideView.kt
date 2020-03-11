package bonch.dev.view.getdriver

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import bonch.dev.R
import bonch.dev.model.getdriver.pojo.PaymentCard
import bonch.dev.model.getdriver.pojo.Ride
import bonch.dev.presenter.getdriver.DetailRidePresenter
import bonch.dev.presenter.getdriver.adapters.PaymentsListAdapter
import bonch.dev.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.utils.Constants.OFFER_PRICE_VIEW
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.detail_ride_layout.*


class DetailRideView(val getDriverView: GetDriverView) {

    var cardsBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var commentBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var detailRidePresenter: DetailRidePresenter? = null
    private var paymentsListAdapter: PaymentsListAdapter? = null
    private val from = getView().view!!.findViewById<TextView>(R.id.from_address)
    private val to = getView().view!!.findViewById<TextView>(R.id.to_address)


    init {
        if (detailRidePresenter == null) {
            detailRidePresenter = DetailRidePresenter(this)
        }
    }


    fun onCreateView(fromAddress: Ride, toAddress: Ride) {
        val activity = getDriverView.activity!!
        val root = getDriverView.view!!

        setListeners(activity, root)

        setBottomSheet(activity, root)

        detailRidePresenter?.receiveAddresses(fromAddress, toAddress)

        initBankCardRecycler()
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OFFER_PRICE_VIEW && resultCode == RESULT_OK) {
            detailRidePresenter?.offerPriceDone(getView().context!!, data)
        }

        if (requestCode == ADD_BANK_CARD_VIEW && resultCode == RESULT_OK) {
            detailRidePresenter?.addBankCardDone(data)
        }
    }


    private fun setListeners(activity: FragmentActivity, root: View) {
        val offerPrice = getView().offer_price
        val addCard = getView().add_card
        val commentBtn = getView().comment_btn
        val commentText = getView().comment_text
        val commentMinText = getView().comment_min_text
        val commentBackBtn = getView().comment_back_btn
        val commentDone = getView().comment_done
        val paymentMethod = getView().payment_method
        val selectedPaymentMethod = getView().selected_payment_method
        val onMapView = getView().on_map_view_detail


        offerPrice.setOnClickListener {
            detailRidePresenter?.offerPrice(getDriverView)

        }

        addCard.setOnClickListener {
            detailRidePresenter?.addBankCard(getDriverView)
        }


        commentBtn.setOnClickListener {
            detailRidePresenter?.commentStart(activity)
        }

        commentBackBtn.setOnClickListener {
            detailRidePresenter?.commentDone(activity, root)
        }

        commentDone.setOnClickListener {
            val comment = commentText.text.toString()

            if (comment.trim().isNotEmpty()) {
                commentMinText.text = comment
            }

            detailRidePresenter?.commentDone(activity, root)
        }

        paymentMethod.setOnClickListener {
            detailRidePresenter?.getPaymentMethods()
        }

        selectedPaymentMethod.setOnClickListener {
            detailRidePresenter?.getPaymentMethods()
        }

        onMapView.setOnClickListener {
            detailRidePresenter?.commentDone(activity, root)
        }
    }


    fun setAddresses(fromAddress: String, toAddress: String) {
        from.text = fromAddress
        to.text = toAddress
    }


    private fun setBottomSheet(activity: FragmentActivity, root: View) {
        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(getView().cards_bottom_sheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(getView().comment_bottom_sheet)

        commentBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                detailRidePresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                detailRidePresenter?.onStateChangedBottomSheet(newState, activity, root)
            }
        })

        cardsBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                detailRidePresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                detailRidePresenter?.onStateChangedBottomSheet(newState, activity, root)
            }
        })
    }


    private fun initBankCardRecycler() {
        val paymentList = getView().payments_list
        val list = arrayListOf<PaymentCard>()

        list.add(PaymentCard(null, null, null, R.drawable.ic_google_pay))
        paymentsListAdapter =
            PaymentsListAdapter(
                paymentList,
                list,
                getDriverView.context!!,
                this
            )

        paymentList.layoutManager =
            LinearLayoutManager(getDriverView.context, LinearLayoutManager.VERTICAL, false)
        paymentList.adapter = paymentsListAdapter

        detailRidePresenter!!.paymentsListAdapter = paymentsListAdapter
    }


    fun removeRoute() {
        detailRidePresenter?.removeRoute()
    }


    fun getView(): GetDriverView {
        return getDriverView
    }


}