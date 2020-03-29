package bonch.dev.view.passanger.getdriver

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.PaymentCard
import bonch.dev.model.passanger.getdriver.pojo.Ride
import bonch.dev.presenter.passanger.getdriver.DetailRidePresenter
import bonch.dev.presenter.passanger.getdriver.adapters.PaymentsListAdapter
import bonch.dev.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.utils.Constants.OFFER_PRICE_VIEW
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.create_ride_fragment.view.*
import kotlinx.android.synthetic.main.detail_ride_layout.view.*


class DetailRideView(val createRideView: CreateRideView) {

    var cardsBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var commentBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var infoPriceBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var detailRidePresenter: DetailRidePresenter? = null
    private var paymentsListAdapter: PaymentsListAdapter? = null
    private val from: TextView
    private val to: TextView


    init {
        detailRidePresenter = DetailRidePresenter(this)

        from = getRootView().findViewById(R.id.from_address)
        to = getRootView().findViewById(R.id.to_address)
    }


    fun onCreateView(fromAddress: Ride, toAddress: Ride) {
        val activity = createRideView.activity!!
        val root = getRootView()

        setListeners(activity, root)

        setBottomSheet(activity, root)

        detailRidePresenter?.receiveAddresses(fromAddress, toAddress)

        initBankCardRecycler()
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OFFER_PRICE_VIEW && resultCode == RESULT_OK) {
            detailRidePresenter?.offerPriceDone(data)
        }

        if (requestCode == ADD_BANK_CARD_VIEW && resultCode == RESULT_OK) {
            detailRidePresenter?.addBankCardDone(data)
        }
    }


    fun removeRoute() {
        detailRidePresenter?.removeRoute()
    }


    private fun setListeners(activity: FragmentActivity, root: View) {
        val offerPrice = getRootView().offer_price
        val addCard = getRootView().add_card
        val getDriverBtn = getRootView().get_driver_btn
        val commentBtn = getRootView().comment_btn
        val commentText = getRootView().comment_text
        val commentMinText = getRootView().comment_min_text
        val commentBackBtn = getRootView().comment_back_btn
        val commentDone = getRootView().comment_done
        val paymentMethod = getRootView().payment_method
        val selectedPaymentMethod = getRootView().selected_payment_method
        val onMapView = getRootView().on_map_view_detail
        val infoPrice = getRootView().info_price
        val backBtn = getRootView().back_btn

        getDriverBtn.setOnClickListener {
            detailRidePresenter?.getDriver()
        }

        offerPrice.setOnClickListener {
            detailRidePresenter?.offerPrice(createRideView)

        }

        addCard.setOnClickListener {
            detailRidePresenter?.addBankCard(createRideView)
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

        infoPrice.setOnClickListener {
            detailRidePresenter?.getInfoPrice()
        }

        backBtn.setOnClickListener {
            getView().backPressed()
        }

    }


    fun setAddresses(fromAddress: String, toAddress: String) {
        from.text = fromAddress
        to.text = toAddress
    }


    private fun setBottomSheet(activity: FragmentActivity, root: View) {
        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(getRootView().cards_bottom_sheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(getRootView().comment_bottom_sheet)
        infoPriceBottomSheetBehavior =
            BottomSheetBehavior.from<View>(getRootView().info_price_bottom_sheet)

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

        infoPriceBottomSheetBehavior!!.addBottomSheetCallback(object :
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
        val paymentList = getRootView().payments_list
        val list = arrayListOf<PaymentCard>()

        list.add(PaymentCard(null, null, null, R.drawable.ic_google_pay))
        paymentsListAdapter =
            PaymentsListAdapter(
                paymentList,
                list,
                createRideView.context!!,
                this
            )

        paymentList.layoutManager =
            LinearLayoutManager(createRideView.context, LinearLayoutManager.VERTICAL, false)
        paymentList.adapter = paymentsListAdapter

        detailRidePresenter!!.paymentsListAdapter = paymentsListAdapter
    }


    fun getView(): CreateRideView {
        return createRideView
    }

    private fun getRootView(): View {
        return createRideView.createRidePresenter?.root!!
    }


}