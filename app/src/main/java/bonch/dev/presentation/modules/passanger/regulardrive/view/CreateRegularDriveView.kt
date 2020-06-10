package bonch.dev.presentation.modules.passanger.regulardrive.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.R
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.domain.entities.common.ride.Address
import bonch.dev.domain.entities.common.ride.Coordinate
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.interfaces.ParentMapHandler
import bonch.dev.presentation.modules.passanger.regulardrive.RegularDriveComponent
import bonch.dev.presentation.modules.passanger.regulardrive.adapters.AddressesListAdapter
import bonch.dev.presentation.modules.passanger.regulardrive.adapters.PaymentsListAdapter
import bonch.dev.presentation.modules.passanger.regulardrive.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.regular_create_ride_layout.*
import java.util.*
import javax.inject.Inject

class CreateRegularDriveView : Fragment(), ContractView.ICreateRegularDriveView {

    @Inject
    lateinit var createRegularDrivePresenter: ContractPresenter.ICreateRegularDrivePresenter

    @Inject
    lateinit var paymentsListAdapter: PaymentsListAdapter

    @Inject
    lateinit var addressesListAdapter: AddressesListAdapter

    private var cardsBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var commentBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var infoPriceBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var daysBottomSheet: BottomSheetBehavior<*>? = null
    private var addressesBottomSheet: BottomSheetBehavior<*>? = null
    private var dateBottomSheet: BottomSheetBehavior<*>? = null
    private var timeBottomSheet: BottomSheetBehavior<*>? = null

    lateinit var mapView: ParentMapHandler<MapView>


    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)

        createRegularDrivePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.regular_create_ride_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBankingAdapter()

        setListeners()

        setBottomSheet()
    }


    override fun setListeners() {

        setListenersAdr()

        listenerSelectDate()

        listenerSelectDays()

        listenerSelectTime()

        from_address.setOnClickListener {
            expandedBottomSheet(true)
        }

        to_address.setOnClickListener {
            expandedBottomSheet(false)
        }

        select_date.setOnClickListener {
            dateBottomSheet?.let { getBottomSheet(it) }
        }

        select_time.setOnClickListener {
            timeBottomSheet?.let { getBottomSheet(it) }
        }

        payment_method.setOnClickListener {
            cardsBottomSheetBehavior?.let { getBottomSheet(it) }
        }

        selected_payment_method.setOnClickListener {
            cardsBottomSheetBehavior?.let { getBottomSheet(it) }
        }

        select_days.setOnClickListener {
            daysBottomSheet?.let { getBottomSheet(it) }
        }

        cancel_days.setOnClickListener {
            hideAllBottomSheet()
        }

        comment_btn.setOnClickListener {
            commentEditStart()
        }

        info_price.setOnClickListener {
            infoPriceBottomSheetBehavior?.let { getBottomSheet(it) }
        }

        show_route.setOnClickListener {
            createRegularDrivePresenter.showRoute()
        }

        comment_back_btn.setOnClickListener {
            commentDone()
        }

        comment_done.setOnClickListener {
            val comment = comment_text.text.toString().trim()
            comment_min_text.text = comment

            commentDone()
        }

        offer_price.setOnClickListener {
            context?.let {
                createRegularDrivePresenter.offerPrice(it, this)
            }
        }

        add_card.setOnClickListener {
            context?.let {
                createRegularDrivePresenter.addBankCard(it, this)
            }
        }

        back_btn.setOnClickListener {
            (activity as? MapCreateRegularDrive)?.finish()
        }

        on_view.setOnClickListener {
            hideAllBottomSheet()
        }
    }


    private fun setListenersAdr() {
        from_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    //  createRegularDrivePresenter.requestSuggest(from_adr.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    from_cross.visibility = View.VISIBLE
                } else {
                    from_cross.visibility = View.GONE
                }
            }
        })

        to_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    //createRegularDrivePresenter.requestSuggest(to_adr.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    to_cross.visibility = View.VISIBLE
                } else {
                    to_cross.visibility = View.GONE
                }
            }
        })
    }


    override fun expandedBottomSheet(isFrom: Boolean) {

        addressesBottomSheet?.let { getBottomSheet(it) }

        if (isFrom) {
            if (to_adr.isFocused) {
                createRegularDrivePresenter.clearSuggest()
            }

            from_adr.requestFocus()
            from_adr.setSelection(from_adr.text.length)

            expandedBottomSheetEvent()

            btn_map_from.visibility = View.VISIBLE
            btn_map_to.visibility = View.GONE
            to_cross.visibility = View.GONE

            if (from_adr.text.isNotEmpty()) {
                from_cross.visibility = View.VISIBLE
            } else {
                //set favourite addresses
                createRegularDrivePresenter.getCashSuggest()
                from_cross.visibility = View.GONE
            }
        } else {
            if (from_adr.isFocused) {
                createRegularDrivePresenter.clearSuggest()
            }

            to_adr.requestFocus()
            to_adr.setSelection(to_adr.text.length)

            expandedBottomSheetEvent()

            btn_map_from.visibility = View.GONE
            btn_map_to.visibility = View.VISIBLE
            from_cross.visibility = View.GONE

            if (to_adr.text.isNotEmpty()) {
                to_cross.visibility = View.VISIBLE
            } else {
                //set favourite addresses
                createRegularDrivePresenter.getCashSuggest()
                to_cross.visibility = View.GONE
            }
        }
    }


    private fun expandedBottomSheetEvent() {
        val activity = activity as? MapCreateRegularDrive

        Handler().postDelayed({
            activity?.let {
                Keyboard.showKeyboard(activity)
            }
        }, 300)
    }


    private fun listenerSelectDate() {
        val arrView: Array<TextView> = arrayOf(every_day, work_day, select_days)

        for (i in arrView.indices) {
            arrView[i].setOnClickListener {

                arrView.forEach {
                    it.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick,
                        0
                    )
                }

                arrView[i].setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_tick_selected,
                    0
                )
            }
        }
    }


    private fun listenerSelectDays() {
        val arrSelectedDays = BooleanArray(7)
        val arrView: Array<TextView> =
            arrayOf(mondey, tuesday, wednesday, thursday, friday, saturday, sunday)

        for (i in arrView.indices) {
            arrView[i].setOnClickListener {

                var icon = R.drawable.ic_selected_item

                try {
                    if (arrSelectedDays[i]) {
                        arrSelectedDays[i] = false
                        icon = R.drawable.ic_unselected_item
                    } else {
                        arrSelectedDays[i] = true
                        icon = R.drawable.ic_selected_item
                    }

                } catch (ex: IndexOutOfBoundsException) {
                }

                arrView[i].setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    icon,
                    0
                )
            }
        }

        save_days.setOnClickListener {
            var daysStr = ""

            for (i in arrSelectedDays.indices) {
                if (arrSelectedDays[i]) {
                    val day = getStrDay(i)
                    daysStr = daysStr.plus(day).plus(" ")
                }
            }

            select_date.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_calendar,
                0,
                0,
                0
            )

            select_date.text = daysStr

            hideAllBottomSheet()
        }
    }


    private fun listenerSelectTime() {
        picker_select_time.setIsAmPm(false)

        val timeZone = GregorianCalendar().timeZone
        val mGMTOffset =
            timeZone.rawOffset + if (timeZone.inDaylightTime(Date())) timeZone.dstSavings else 0

        save_time.setOnClickListener {
            val milliseconds = picker_select_time?.date?.time?.plus(mGMTOffset)

            if (milliseconds != null) {
                val minutes = ((milliseconds / (1000 * 60)) % 60)
                val hours = ((milliseconds / (1000 * 60 * 60)) % 24)

                var min = minutes.toString()
                var hour = hours.toString()
                if (min.length == 1) min = "0".plus(min)
                if (hour.length == 1) hour = "0".plus(hour)

                select_time.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_clock,
                    0,
                    0,
                    0
                )

                select_time.text = hour.plus(":").plus(min)
            }

            hideAllBottomSheet()
        }
    }


    override fun showStartUI() {
        center_position.setImageResource(R.drawable.ic_map_marker)
        main_addresses_layout.visibility = View.VISIBLE
        addresses_list.visibility = View.VISIBLE
        address_map_marker_layout.visibility = View.GONE
        back_btn.visibility = View.GONE
        addressesBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun setAddressView(isFrom: Boolean, address: String) {
        if (isFrom) {
            from_adr.setText(address)
        } else {
            to_adr.setText(address)
        }

        address_map_text.text = address
    }


    override fun getActualFocus(): Boolean {
        return (address_map_marker_layout.isVisible)
    }


    override fun removeAddressesView(isFrom: Boolean) {
        if (isFrom) {
            from_adr.setText("")
//            not allow user to remove 'from' address
//            fromAdr = null
        } else {
            to_adr.setText("")
        }
    }


    override fun addressesMapViewChanged() {
        main_addresses_layout.visibility = View.GONE
        addresses_list.visibility = View.GONE
        address_map_marker_layout.visibility = View.VISIBLE
        back_btn.visibility = View.VISIBLE

        address_map_text.isSelected = true
        center_position.setImageResource(R.drawable.ic_map_marker_black)
        circle_marker.setImageResource(R.drawable.ic_input_marker_to)
        address_map_marker_btn.setBackgroundResource(R.drawable.bg_btn_black)

        addressesBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        hideKeyboard()
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
            commentBottomSheetBehavior,
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


        //set correct height
        val display = (activity as? MapCreateRegularDrive)?.windowManager?.defaultDisplay
        val size = android.graphics.Point()
        display?.getSize(size)
        val height = size.y
        val layoutParams: ViewGroup.LayoutParams = bottom_sheet_addresses.layoutParams
        layoutParams.height = height - 200
        bottom_sheet_addresses.layoutParams = layoutParams
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
                val activity = activity as? MapCreateRegularDrive
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


    private fun getStrDay(idDay: Int): String {
        return when (idDay) {
            0 -> getString(R.string.mondayC)
            1 -> getString(R.string.tuesdayC)
            2 -> getString(R.string.wednesdayC)
            3 -> getString(R.string.thursdayC)
            4 -> getString(R.string.fridayC)
            5 -> getString(R.string.saturdayC)
            6 -> getString(R.string.sundayC)
            else -> getString(R.string.mondayC)
        }
    }


    override fun onClickItem(address: Address, isFromMapSearch: Boolean) {
        if (isFromMapSearch) {
            from_adr.setText(address.address)
            from_adr.setSelection(from_adr.text.length)
            Coordinate.fromAdr = address
        } else {
            to_adr.setText(address.address)
            to_adr.setSelection(to_adr.text.length)
            Coordinate.toAdr = address
        }
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


    override fun getAddressesAdapter(): AddressesListAdapter {
        return addressesListAdapter
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