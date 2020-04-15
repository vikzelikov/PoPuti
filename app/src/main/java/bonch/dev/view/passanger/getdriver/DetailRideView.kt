package bonch.dev.view.passanger.getdriver

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.toAdr
import bonch.dev.model.passanger.getdriver.pojo.PaymentCard
import bonch.dev.presenter.passanger.getdriver.DetailRidePresenter
import bonch.dev.presenter.passanger.getdriver.adapters.PaymentsListAdapter
import bonch.dev.utils.Constants.ADD_BANK_CARD_VIEW
import bonch.dev.utils.Constants.OFFER_PRICE_VIEW
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.create_ride_fragment.*


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

        from = getView().from_address
        to = getView().to_address
    }


    fun onCreateView() {
        setListeners()

        setBottomSheet()

        detailRidePresenter?.receiveAddresses(fromAdr, toAdr)

        initBankCardRecycler()

        //it is fix bug with hidden bottom nav menu
        //TODO
        getView().on_map_view.visibility = View.VISIBLE
        getView().on_map_view.visibility = View.GONE
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


    private fun setListeners() {
        val r = getView()

        r.get_driver_btn.setOnClickListener {
            detailRidePresenter?.getDriver()
        }

        r.offer_price.setOnClickListener {
            detailRidePresenter?.offerPrice(createRideView)

        }

        r.add_card.setOnClickListener {
            detailRidePresenter?.addBankCard(createRideView)
        }


        r.comment_btn.setOnClickListener {
            detailRidePresenter?.commentStart()
        }

        r.comment_back_btn.setOnClickListener {
            detailRidePresenter?.commentDone()
        }

        r.comment_done.setOnClickListener {
            val comment = r.comment_text.text.toString()

            if (comment.trim().isNotEmpty()) {
                r.comment_min_text.text = comment
            }

            detailRidePresenter?.commentDone()
        }

        r.payment_method.setOnClickListener {
            detailRidePresenter?.getPaymentMethods()
        }

        r.selected_payment_method.setOnClickListener {
            detailRidePresenter?.getPaymentMethods()
        }

        r.on_map_view_detail.setOnClickListener {
            detailRidePresenter?.commentDone()
        }

        r.info_price.setOnClickListener {
            detailRidePresenter?.getInfoPrice()
        }

        r.show_route.setOnClickListener {
            detailRidePresenter?.showRoute()
        }

        r.back_btn.setOnClickListener {
            getView().backPressed()
        }
    }


    fun setAddresses(fromAddress: String, toAddress: String) {
        from.text = fromAddress
        to.text = toAddress
    }


    private fun setBottomSheet() {
        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(getView().cards_bottom_sheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(getView().comment_bottom_sheet)
        infoPriceBottomSheetBehavior =
            BottomSheetBehavior.from<View>(getView().info_price_bottom_sheet)

        commentBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                detailRidePresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                detailRidePresenter?.onStateChangedBottomSheet(newState)
            }
        })

        cardsBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                detailRidePresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                detailRidePresenter?.onStateChangedBottomSheet(newState)
            }
        })

        infoPriceBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                detailRidePresenter?.onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                detailRidePresenter?.onStateChangedBottomSheet(newState)
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
                createRideView.context!!,
                this
            )

        paymentList.layoutManager =
            LinearLayoutManager(createRideView.context, LinearLayoutManager.VERTICAL, false)
        paymentList.adapter = paymentsListAdapter

        detailRidePresenter!!.paymentsListAdapter = paymentsListAdapter
    }


    fun backPressed(): Boolean {
        return detailRidePresenter?.backPressed()!!
    }


    fun getView(): CreateRideView {
        return createRideView
    }
}