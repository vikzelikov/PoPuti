package bonch.dev.presentation.modules.passanger.regulardrive.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.interfaces.ParentMapHandler
import bonch.dev.presentation.modules.passanger.regulardrive.adapters.PaymentsListAdapter
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.regular_create_ride_layout.*
import kotlinx.android.synthetic.main.regular_create_ride_layout.back_btn
import kotlinx.android.synthetic.main.regular_create_ride_layout.cards_bottom_sheet
import kotlinx.android.synthetic.main.regular_create_ride_layout.comment_bottom_sheet
import kotlinx.android.synthetic.main.regular_create_ride_layout.comment_text
import kotlinx.android.synthetic.main.regular_create_ride_layout.info_price
import kotlinx.android.synthetic.main.regular_create_ride_layout.info_price_bottom_sheet
import kotlinx.android.synthetic.main.regular_create_ride_layout.main_info_layout
import kotlinx.android.synthetic.main.regular_create_ride_layout.number_card
import kotlinx.android.synthetic.main.regular_create_ride_layout.offer_price
import kotlinx.android.synthetic.main.regular_create_ride_layout.payment_method
import kotlinx.android.synthetic.main.regular_create_ride_layout.payment_method_img
import kotlinx.android.synthetic.main.regular_create_ride_layout.payments_list
import kotlinx.android.synthetic.main.regular_create_ride_layout.selected_payment_method
import kotlinx.android.synthetic.main.regular_create_ride_layout.show_route
import javax.inject.Inject

class CreateRegularDriveView : Fragment(), ContractView.ICreateRegularDriveView {

    @Inject
    lateinit var createRegularDrivePresenter: ContractPresenter.ICreateRegularDrivePresenter

    @Inject
    lateinit var paymentsListAdapter: PaymentsListAdapter

    private var cardsBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var commentBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var infoPriceBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var daysBottomSheet: BottomSheetBehavior<*>? = null
    private var addressesBottomSheet: BottomSheetBehavior<*>? = null
    private var dateBottomSheet: BottomSheetBehavior<*>? = null
    private var timeBottomSheet: BottomSheetBehavior<*>? = null

    lateinit var mapView: ParentMapHandler<MapView>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.regular_create_ride_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        setBottomSheet()
    }


    override fun setListeners() {

        select_date.setOnClickListener {
            dateBottomSheet?.let { getBottomSheet(it) }
        }

        select_time.setOnClickListener {
            timeBottomSheet?.let { getBottomSheet(it) }
        }

        payment_method.setOnClickListener {
            cardsBottomSheetBehavior?.let { getBottomSheet(it) }
        }

        select_days.setOnClickListener {
            daysBottomSheet?.let { getBottomSheet(it) }
        }

        comment_btn.setOnClickListener{
            commentEditStart()
        }

        offer_price.setOnClickListener {

        }

        back_btn.setOnClickListener {
            (activity as? MapCreateRegularDrive)?.finish()
        }

        on_view.setOnClickListener {
            hideAllBottomSheet()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == createRegularDrivePresenter.instance().OFFER_PRICE && resultCode == Activity.RESULT_OK) {
            createRegularDrivePresenter.offerPriceDone(data)
        }

        if (requestCode == createRegularDrivePresenter.instance().ADD_BANK_CARD && resultCode == Activity.RESULT_OK) {
            createRegularDrivePresenter.addBankCardDone(data)
        }
    }


    private fun initBankingAdapter() {
        val list = arrayListOf<BankCard>()

        //add google pay
        list.add(
            BankCard(
                null,
                null,
                null,
                R.drawable.ic_google_pay
            )
        )
        paymentsListAdapter.list = list

        payments_list.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentsListAdapter
        }
    }


    private fun setBottomSheet() {
        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(cards_bottom_sheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(comment_bottom_sheet)
        infoPriceBottomSheetBehavior = BottomSheetBehavior.from<View>(info_price_bottom_sheet)
        daysBottomSheet = BottomSheetBehavior.from<View>(days_bottom_sheet)
        addressesBottomSheet = BottomSheetBehavior.from<View>(bottom_sheet_addresses)
        dateBottomSheet = BottomSheetBehavior.from<View>(select_date_bottom_sheet)
        timeBottomSheet = BottomSheetBehavior.from<View>(select_time_bottom_sheet)

        val arrView: Array<BottomSheetBehavior<*>?> = arrayOf(
            cardsBottomSheetBehavior,
            cardsBottomSheetBehavior,
            infoPriceBottomSheetBehavior,
            daysBottomSheet,
            addressesBottomSheet,
            dateBottomSheet,
            timeBottomSheet
        )

        arrView.forEach {
            it?.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    onSlideBottomSheet(slideOffset)
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    onStateChangedBottomSheet(newState)
                }
            })
        }
    }


    override fun getBottomSheet(bottomSheetBehavior: BottomSheetBehavior<*>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun hideAllBottomSheet() {
        hideKeyboard()
        comment_text.clearFocus()

        commentBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        infoPriceBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        daysBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        addressesBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        dateBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        timeBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    private fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0 && slideOffset < 1) {
            on_view.alpha = slideOffset * 0.8f
            back_btn.alpha = 1 - slideOffset
            show_route.alpha = 1 - slideOffset
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard()
            comment_text.clearFocus()
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view.visibility = View.GONE
            main_info_layout.elevation = 30f
            comment_text.clearFocus()
            hideAllBottomSheet()
        } else {
            on_view.visibility = View.VISIBLE
            main_info_layout.elevation = 0f
        }
    }


    override fun isDataComplete(): Boolean {
        val isComplete: Boolean
        val offerPrice = offer_price.text.toString().trim()

        if (selected_payment_method.visibility == View.VISIBLE && offerPrice.length < 5) {
            isComplete = true
            changeBtnEnable(true)
        } else {
            isComplete = false
            changeBtnEnable(false)
        }

        return isComplete
    }


    override fun offerPriceDone(price: Int, averagePrice: Int) {
        offer_price.textSize = 22f
        offer_price.setPadding(0, 5, 0, 25)
        offer_price.setTextColor(Color.parseColor("#000000"))
        offer_price.typeface = Typeface.DEFAULT_BOLD

        if (averagePrice <= price) {
            info_price.visibility = View.GONE
        } else {
            info_price.visibility = View.VISIBLE
        }

        try {
            offer_price.text = price.toString()
        } catch (ex: NumberFormatException) {
            offer_price.text = "0"
        }

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun setSelectedBankCard(bankCard: BankCard) {
        selected_payment_method.visibility = View.VISIBLE
        payment_method.visibility = View.GONE

        number_card.text = bankCard.numberCard
        val img = bankCard.img
        if (img != null) {
            payment_method_img.setImageResource(img)
        }

        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun removeTickSelected() {
        val childCount = payments_list.childCount

        for (i in 0 until childCount) {
            val holder = payments_list.getChildViewHolder(payments_list.getChildAt(i))
            val tick = holder.itemView.findViewById<ImageView>(R.id.tick)
            tick.setImageResource(R.drawable.ic_tick)
        }
    }


    override fun commentEditStart() {
        commentBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        if (!comment_text.isFocused) {
            comment_text.requestFocus()
            //set a little timer to open keyboard
            Handler().postDelayed({
                val activity = activity as? MainActivity
                activity?.let {
                    Keyboard.showKeyboard(activity)
                }
            }, 200)
        }
    }


    private fun commentDone() {
        comment_text?.clearFocus()

        hideAllBottomSheet()
    }


    private fun changeBtnEnable(isEnable: Boolean) {
        if (isEnable) {
            save_ride.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            save_ride.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun getPaymentsAdapter(): PaymentsListAdapter {
        return paymentsListAdapter
    }


    override fun getNavHost(): NavController? {
        return null
    }


    override fun getMap(): MapView? {
        return mapView()
    }


    override fun hideKeyboard() {
        val activity = activity as? MapCreateRegularDrive
        activity?.let {
            Keyboard.hideKeyboard(activity, view)
        }
    }


}