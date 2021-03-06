package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.Coordinate
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.utils.ChangeOpacity
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.base.MBottomSheet
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.interfaces.ParentMapHandler
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters.AddressesListAdapter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters.PaymentsListAdapter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.regular_create_ride_layout.*
import kotlinx.android.synthetic.main.regular_create_ride_layout.address_map_marker_btn
import kotlinx.android.synthetic.main.regular_create_ride_layout.address_map_marker_layout
import kotlinx.android.synthetic.main.regular_create_ride_layout.address_map_text
import kotlinx.android.synthetic.main.regular_create_ride_layout.addresses_list
import kotlinx.android.synthetic.main.regular_create_ride_layout.back_btn
import kotlinx.android.synthetic.main.regular_create_ride_layout.bottom_sheet_addresses
import kotlinx.android.synthetic.main.regular_create_ride_layout.bottom_sheet_arrow
import kotlinx.android.synthetic.main.regular_create_ride_layout.btn_map_from
import kotlinx.android.synthetic.main.regular_create_ride_layout.btn_map_to
import kotlinx.android.synthetic.main.regular_create_ride_layout.center_position
import kotlinx.android.synthetic.main.regular_create_ride_layout.circle_marker
import kotlinx.android.synthetic.main.regular_create_ride_layout.from_adr
import kotlinx.android.synthetic.main.regular_create_ride_layout.from_cross
import kotlinx.android.synthetic.main.regular_create_ride_layout.main_addresses_layout
import kotlinx.android.synthetic.main.regular_create_ride_layout.to_adr
import kotlinx.android.synthetic.main.regular_create_ride_layout.to_cross
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateRegularRideView : Fragment(), ContractView.ICreateRegularDriveView {

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
    private var mainInfoBottomSheet: BottomSheetBehavior<*>? = null

    var myCityCallback: ParentHandler<Address>? = null
    var userPoint: ParentMapHandler<Point?>? = null

    lateinit var moveMapCamera: ParentHandler<Point>
    lateinit var zoomMap: ParentHandler<CameraPosition>
    lateinit var mapView: ParentMapHandler<MapView>

    private var isUpMoved = true

    private val VISA = 4
    private val MC = 5
    private val RUS_WORLD = 2

    private var arrSelectedDays = BooleanArray(7)


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
        super.onViewCreated(view, null)

        initBankingAdapter()

        initAdapter()

        setListeners()

        setBottomSheet()

        createRegularDrivePresenter.checkOnEditRide()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun setListeners() {

        setListenersAdr()

        listenerSelectDate()

        listenerSelectDays()

        listenerSelectTime()

        save_ride.setOnClickListener {
            if (ActiveRide.activeRide == null) {
                createRegularDrivePresenter.createRide()
            } else {
                createRegularDrivePresenter.updateRide()
            }
        }

        from_cross.setOnClickListener {
            createRegularDrivePresenter.touchCrossAddress(true)
        }

        to_cross.setOnClickListener {
            createRegularDrivePresenter.touchCrossAddress(false)
        }

        from_adr.setOnTouchListener { _, _ ->
            createRegularDrivePresenter.touchAddress(isFrom = true, isShowKeyboard = false)
            false
        }

        from_address.setOnClickListener {
            createRegularDrivePresenter.touchAddress(isFrom = true, isShowKeyboard = true)
        }

        to_adr.setOnTouchListener { _, _ ->
            createRegularDrivePresenter.touchAddress(isFrom = false, isShowKeyboard = false)
            false
        }

        to_address.setOnClickListener {
            createRegularDrivePresenter.touchAddress(isFrom = false, isShowKeyboard = true)
        }

        select_date.setOnClickListener {
            if (select_date?.text == getString(R.string.selectDate)) {
                val arrView: Array<TextView?> = arrayOf(every_day, work_day, select_days)

                arrView.forEach {
                    it?.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick,
                        0
                    )
                }
            }

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
            hideAllBottomSheet()
            hideMainInfoLayout()

            createRegularDrivePresenter.showRoute()
        }

        show_my_position.setOnClickListener {
            hideAllBottomSheet()
            hideMainInfoLayout()

            createRegularDrivePresenter.showMyPosition()

            (activity as? MapCreateRegularRide)?.let {
                Geo.showAlertEnable(it)
            }
        }

        show_my_position_c.setOnClickListener {
            hideAllBottomSheet()
            hideMainInfoLayout()

            createRegularDrivePresenter.showMyPosition()

            (activity as? MapCreateRegularRide)?.let {
                Geo.showAlertEnable(it)
            }
        }

        comment_back_btn.setOnClickListener {
            commentDone()
        }

        comment_done.setOnClickListener {
            comment_text?.text?.toString()?.trim()?.let {
                setComment(it)
            }

            commentDone()
        }

        offer_price.setOnClickListener {
            context?.let {
                if (checkAddress()) {
                    createRegularDrivePresenter.offerPrice(it, this)
                } else showNotification(getString(R.string.setAddress))
            }
        }

        add_card.setOnClickListener {
            context?.let {
                createRegularDrivePresenter.addBankCard(it, this)
            }
        }

        back_btn.setOnClickListener {
            if (onBackPressed()) (activity as? MapCreateRegularRide)?.finish()
        }

        on_view.setOnClickListener {
            hideAllBottomSheet()
        }

        btn_map_from.setOnClickListener {
            createRegularDrivePresenter.touchMapBtn(true)
        }

        btn_map_to.setOnClickListener {
            createRegularDrivePresenter.touchMapBtn(false)
        }

        address_map_marker_btn.setOnClickListener {
            clearInfoData()

            createRegularDrivePresenter.touchAddressMapMarkerBtn()
        }
    }


    private fun setListenersAdr() {
        from_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED && !checkCompleteAddresses()) {
                    createRegularDrivePresenter.requestSuggest(from_adr?.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    from_cross?.visibility = View.VISIBLE
                } else {
                    from_cross?.visibility = View.GONE
                }
            }
        })

        to_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED && !checkCompleteAddresses()) {
                    createRegularDrivePresenter.requestSuggest(to_adr?.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && addressesBottomSheet?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    to_cross?.visibility = View.VISIBLE
                } else {
                    to_cross?.visibility = View.GONE
                }
            }
        })
    }


    private fun checkCompleteAddresses(): Boolean {
        return if (Coordinate.fromAdr != null && Coordinate.toAdr != null) {
            hideKeyboard()
            true
        } else false
    }


    override fun expandedBottomSheet(isFrom: Boolean, isShowKeyboard: Boolean) {
        addressesBottomSheet?.let { getBottomSheet(it) }
        from_adr?.isSingleLine = true
        to_adr?.isSingleLine = true

        if (isFrom) {
            to_adr?.let {
                if (it.isFocused) {
                    createRegularDrivePresenter.clearSuggest()
                }
            }

            from_adr?.requestFocus()
            from_adr?.setSelection(from_adr.text.length)

            expandedBottomSheetEvent(isShowKeyboard)

            btn_map_from?.visibility = View.VISIBLE
            btn_map_to?.visibility = View.GONE
            to_cross?.visibility = View.GONE

            from_adr?.text?.let {
                if (it.isNotEmpty()) {
                    from_cross?.visibility = View.VISIBLE
                } else {
                    //set favourite addresses
                    createRegularDrivePresenter.getCashSuggest()
                    from_cross?.visibility = View.GONE
                }
            }
        } else {
            Coordinate.fromAdr?.address?.let { setAddressView(true, it) }

            from_adr?.let {
                if (it.isFocused) {
                    createRegularDrivePresenter.clearSuggest()
                }
            }

            to_adr?.requestFocus()
            to_adr?.setSelection(to_adr.text.length)

            expandedBottomSheetEvent(isShowKeyboard)

            btn_map_from?.visibility = View.GONE
            btn_map_to?.visibility = View.VISIBLE
            from_cross?.visibility = View.GONE

            to_adr?.text?.let {
                if (it.isNotEmpty()) {
                    to_cross?.visibility = View.VISIBLE
                } else {
                    //set favourite addresses
                    createRegularDrivePresenter.getCashSuggest()
                    to_cross?.visibility = View.GONE
                }
            }
        }
    }


    private fun expandedBottomSheetEvent(isShowKeyboard: Boolean) {
        if (isShowKeyboard) {
            val activity = activity as? MapCreateRegularRide

            Handler().postDelayed({
                activity?.let {
                    Keyboard.showKeyboard(activity)
                }
            }, 300)
        }
    }


    private fun listenerSelectDate() {
        val arrView: Array<TextView?> = arrayOf(every_day, work_day, select_days)

        for (i in arrView.indices) {
            arrView[i]?.setOnClickListener {

                if (arrView[i] != select_days) {
                    arrView.forEach {
                        it?.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_tick,
                            0
                        )
                    }


                    arrView[i]?.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_tick_selected,
                        0
                    )
                }

                if (arrView[i] == every_day) {
                    val arrDays = booleanArrayOf(true, true, true, true, true, true, true)
                    setDays(arrDays)

                    dateBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                if (arrView[i] == work_day) {
                    val arrDays = booleanArrayOf(true, true, true, true, true, false, false)
                    setDays(arrDays)

                    dateBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                if (arrView[i] == select_days) {
                    daysBottomSheet?.let {
                        //update days
                        arrSelectedDays = getDays()

                        val arrDaysTics: Array<TextView?> =
                            arrayOf(mondey, tuesday, wednesday, thursday, friday, saturday, sunday)

                        arrDaysTics.forEachIndexed { i, view ->
                            var icon = R.drawable.ic_unselected_item

                            if (arrSelectedDays[i]) {
                                icon = R.drawable.ic_selected_item
                            }

                            view?.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                icon,
                                0
                            )
                        }

                        getBottomSheet(it)
                    }
                }

                isDataComplete()
            }
        }
    }


    private fun listenerSelectDays() {
        val arrView: Array<TextView?> =
            arrayOf(mondey, tuesday, wednesday, thursday, friday, saturday, sunday)

        for (i in arrView.indices) {
            arrView[i]?.setOnClickListener {
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

                arrView[i]?.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    icon,
                    0
                )
            }
        }

        save_days?.setOnClickListener {
            setDays(arrSelectedDays)

            every_day?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick, 0)
            work_day?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick, 0)
            select_days?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_tick_selected,
                0
            )

            hideAllBottomSheet()

            isDataComplete()
        }
    }


    override fun setDays(arrSelectedDays: BooleanArray) {
        var daysStr = ""

        for (i in arrSelectedDays.indices) {
            if (arrSelectedDays[i]) {
                val day = getStrDay(i)
                daysStr = daysStr.plus(day).plus(" ")
            }
        }

        if (daysStr.trim().isEmpty()) {
            select_date?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_right,
                0
            )

            select_date?.text = getString(R.string.selectDate)
        } else {
            select_date?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_calendar,
                0,
                0,
                0
            )

            select_date?.text = daysStr
        }
    }


    override fun setTime(time: String) {
        select_time?.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_clock,
            0,
            0,
            0
        )
        select_time?.text = time
    }


    private fun listenerSelectTime() {
        picker_select_time?.setIsAmPm(false)

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

                select_time?.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_clock,
                    0,
                    0,
                    0
                )

                val time = hour.plus(":").plus(min)
                setTime(time)
            }

            hideAllBottomSheet()
        }
    }


    override fun showStartUI(isShowBottomSheet: Boolean) {
        val shape = GradientDrawable()
        val corners = floatArrayOf(24f, 24f, 24f, 24f, 0f, 0f, 0f, 0f)
        shape.cornerRadii = corners
        shape.setColor(Color.parseColor("#${ChangeOpacity.getOpacity(100)}FFFFFF"))
        bottom_sheet_addresses?.background = shape
        bottom_sheet_arrow?.visibility = View.VISIBLE

        addressesBottomSheet?.peekHeight = 0
        main_info_layout?.visibility = View.VISIBLE
        (addressesBottomSheet as? MBottomSheet<*>)?.swipeEnabled = true

        center_position?.setImageResource(R.drawable.ic_map_marker)
        main_addresses_layout?.visibility = View.VISIBLE
        addresses_list?.visibility = View.VISIBLE
        address_map_marker_layout?.visibility = View.GONE

        val isOnRouting = createRegularDrivePresenter.instance().isOnRouting
        createRegularDrivePresenter.instance().isBlockGeocoder = if (isOnRouting) {
            hideMapMarker()
            true
        } else {
            showMapMarker()
            false
        }

        if (isShowBottomSheet)
            addressesBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun initAdapter() {
        addresses_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = addressesListAdapter
        }
    }


    override fun setAddressView(isFrom: Boolean, address: String) {
        if (isFrom) {
            from_adr?.setText(address)
            from_address?.text = address
        } else {
            to_adr?.setText(address)
            to_address?.text = address
        }

        address_map_text?.text = address

        isDataComplete()
    }


    override fun getActualFocus(): Boolean? {
        return (address_map_marker_layout?.isVisible)
    }


    override fun removeAddressesView(isFrom: Boolean) {
        if (isFrom) {
            from_adr?.setText("")
            from_address?.text = ""
//            not allow user to remove 'from' address
//            fromAdr = null
        } else {
            clearInfoData()

            to_adr?.setText("")
            to_address?.text = ""
        }

        isDataComplete()
    }


    override fun addressesMapViewChanged(isFrom: Boolean) {
        val shape = GradientDrawable()
        val corners = floatArrayOf(24f, 24f, 24f, 24f, 0f, 0f, 0f, 0f)
        shape.cornerRadii = corners
        shape.setColor(Color.parseColor("#${ChangeOpacity.getOpacity(0)}FFFFFF"))
        bottom_sheet_addresses?.background = shape
        bottom_sheet_arrow?.visibility = View.GONE

        getDP(240)?.let { addressesBottomSheet?.peekHeight = it }
        main_info_layout?.visibility = View.GONE
        (addressesBottomSheet as? MBottomSheet<*>)?.swipeEnabled = false

        main_addresses_layout?.visibility = View.GONE
        addresses_list?.visibility = View.GONE
        address_map_marker_layout?.visibility = View.VISIBLE
        back_btn?.visibility = View.VISIBLE
        address_map_text?.isSelected = true

        createRegularDrivePresenter.instance().isBlockGeocoder = false
        clearInfoData()
        showMapMarker()

        if (isFrom) {
            center_position?.setImageResource(R.drawable.ic_map_marker)
            circle_marker?.setImageResource(R.drawable.ic_input_marker_from)
            address_map_marker_btn?.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            center_position?.setImageResource(R.drawable.ic_map_marker_black)
            circle_marker?.setImageResource(R.drawable.ic_input_marker_to)
            address_map_marker_btn?.setBackgroundResource(R.drawable.bg_btn_black)
        }

        addressesBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        hideKeyboard()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == createRegularDrivePresenter.instance().OFFER_PRICE && resultCode == RESULT_OK) {
            createRegularDrivePresenter.offerPriceDone(data)
        }

        if (requestCode == createRegularDrivePresenter.instance().ADD_BANK_CARD && resultCode == RESULT_OK) {
            createRegularDrivePresenter.addBankCardDone(data)
        }
    }


    private fun initBankingAdapter() {
        payments_list?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = paymentsListAdapter
        }

        val cards = createRegularDrivePresenter.getBankCards()
        if (cards.isNotEmpty()) {
            cards.sortBy {
                it.id
            }

            paymentsListAdapter.list.addAll(cards)
            paymentsListAdapter.notifyDataSetChanged()

            payments_list?.visibility = View.VISIBLE
        }

        cards.forEach {
            if (it.isSelect) setSelectedBankCard(it)
        }
    }


    private fun setBottomSheet() {
        mainInfoBottomSheet = BottomSheetBehavior.from<View>(main_info_layout)
        cardsBottomSheetBehavior = BottomSheetBehavior.from<View>(cards_bottom_sheet)
        commentBottomSheetBehavior = BottomSheetBehavior.from<View>(comment_bottom_sheet)
        infoPriceBottomSheetBehavior = BottomSheetBehavior.from<View>(info_price_bottom_sheet)
        daysBottomSheet = BottomSheetBehavior.from<View>(days_bottom_sheet)
        addressesBottomSheet = BottomSheetBehavior.from<View>(bottom_sheet_addresses)
        dateBottomSheet = BottomSheetBehavior.from<View>(select_date_bottom_sheet)
        timeBottomSheet = BottomSheetBehavior.from<View>(select_time_bottom_sheet)

        (mainInfoBottomSheet as? MBottomSheet<*>)?.swipeEnabled = true

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
            if (it != daysBottomSheet) {
                it?.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        onSlideBottomSheet(slideOffset)
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        onStateChangedBottomSheet(newState)
                    }
                })
            } else {
                it?.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        onSlideBottomSheet(slideOffset)
                    }

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        onStateDaysBottomSheet(newState)
                    }
                })
            }
        }

        mainInfoBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideMainBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
        })

        //set correct height
        val display = (activity as? MapCreateRegularRide)?.windowManager?.defaultDisplay
        val size = android.graphics.Point()
        display?.getSize(size)
        val height = size.y
        val layoutParams = bottom_sheet_addresses?.layoutParams
        layoutParams?.height = height - 200
        bottom_sheet_addresses?.layoutParams = layoutParams
    }


    override fun getBottomSheet(bottomSheetBehavior: BottomSheetBehavior<*>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun hideAllBottomSheet() {
        hideKeyboard()
        comment_text?.clearFocus()

        commentBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        infoPriceBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        daysBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        addressesBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        dateBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        timeBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED

        on_view?.visibility = View.GONE
        Handler().postDelayed({
            select_date_bottom_sheet?.visibility = View.VISIBLE
        }, 500)
    }


    override fun hideMainInfoLayout() {
        mainInfoBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    override fun showMainInfoLayout() {
        mainInfoBottomSheet?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0 && slideOffset < 1) {
            on_view?.alpha = slideOffset * 0.8f
            addresses_list?.alpha = 1.0f
        }
    }


    private fun onSlideMainBottomSheet(slideOffset: Float) {
        if (slideOffset > 0 && slideOffset < 1) {
            center_position?.alpha = 1 - slideOffset
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard()
            comment_text?.clearFocus()
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            Coordinate.fromAdr?.address?.let { setAddressView(true, it) }

            on_view?.visibility = View.GONE
            comment_text?.clearFocus()
        } else {
            on_view?.visibility = View.VISIBLE
        }
    }


    private fun onStateDaysBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            hideKeyboard()
            comment_text?.clearFocus()

            select_date_bottom_sheet?.visibility = View.GONE
        }

        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view?.visibility = View.GONE
            comment_text?.clearFocus()

            hideAllBottomSheet()
        } else {
            on_view?.visibility = View.VISIBLE
        }
    }


    override fun getRideInfo(): RideInfo {
        val rideInfo = RideInfo()
        try {
            val comment = comment_min_text?.text?.let {
                if (it.isEmpty()) {
                    null
                } else {
                    comment_text?.text?.toString()?.trim()
                }
            }
            val price = offer_price?.text?.toString()?.toInt()

            rideInfo.comment = comment
            rideInfo.price = price
        } catch (ex: NumberFormatException) {
            Log.e("Convert", "Converting price ride to Int " + this::class.java.simpleName)
        }

        return rideInfo
    }


    override fun isDataComplete(): Boolean {
        var isComplete = false

        if (selected_payment_method?.visibility == View.VISIBLE
            && checkPrice()
            && checkAddress()
            && checkDays()
        ) {
            isComplete = true
        }

        changeBtnEnable(isComplete)

        return isComplete
    }


    private fun checkPrice(): Boolean {
        var result = false

        try {
            val price = offer_price?.text?.toString()?.trim()?.toInt()
            if (price != null && price > 0) {
                result = true
            }
        } catch (ex: NumberFormatException) {

        }

        return result
    }


    private fun checkDays(): Boolean {
        var result = false
        getDays().forEach {
            if (it) result = true
        }

        return result
    }


    private fun checkAddress(): Boolean {
        var result = false
        if (Coordinate.fromAdr != null && Coordinate.toAdr != null)
            result = true
        return result
    }


    override fun requestGeocoder(cameraPosition: CameraPosition, isUp: Boolean) {
        hideMainInfoLayout()

        if (isUp) {
            if (isUpMoved) {
                center_position?.animate()
                    ?.setDuration(250L)
                    ?.translationY(-50f)

                shadow_marker?.animate()
                    ?.setDuration(150L)
                    ?.alpha(0.6f)

                isUpMoved = false
            }
        } else {
            isUpMoved = true

            zoomMap(cameraPosition)

            shadow_marker?.animate()
                ?.setDuration(150L)
                ?.alpha(0.3f)

            center_position?.animate()
                ?.setDuration(250L)
                ?.translationY(0f)
                ?.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        center_position?.let {
                            if (it.translationY == 0.0f) {
                                createRegularDrivePresenter.requestGeocoder(
                                    Point(
                                        cameraPosition.target.latitude,
                                        cameraPosition.target.longitude
                                    )
                                )
                            }
                        }
                    }
                })
        }
    }


    override fun offerPriceDone(price: Int) {
        offer_price?.textSize = 22f
        getDP(15)?.let { offer_price?.setPadding(0, 5, 0, it) }
        offer_price?.typeface = Typeface.DEFAULT_BOLD

        val averagePrice = Coordinate.averagePrice
        if (averagePrice != null) {
            if (averagePrice <= price) {
                info_price?.visibility = View.GONE
            } else {
                info_price?.visibility = View.VISIBLE
            }
        }

        try {
            offer_price?.text = price.toString()
        } catch (ex: NumberFormatException) {
            offer_price?.text = "0"
        }

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    private fun resetPrice() {
        val top = getDP(12)
        val bottom = getDP(25)
        if (top != null && bottom != null)
            offer_price?.setPadding(0, top, 0, bottom)


        offer_price?.textSize = 14f
        offer_price?.typeface = Typeface.DEFAULT

        info_price?.visibility = View.GONE

        offer_price?.text = ""


        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun setSelectedBankCard(bankCard: BankCard) {
        val hideChars = "•••• "
        var imgCard: Int? = null
        var numberCard = bankCard.numberCard

        if (numberCard != null) {
            val firstDigit = numberCard.first().minus('0')

            try {
                numberCard = when (firstDigit) {
                    VISA -> {
                        imgCard = R.drawable.ic_visa
                        hideChars + numberCard.substring(15, 19)
                    }

                    MC -> {
                        imgCard = R.drawable.ic_mastercard
                        hideChars + numberCard.substring(15, 19)
                    }

                    RUS_WORLD -> {
                        imgCard = R.drawable.ic_pay_world
                        hideChars + numberCard.substring(15, 19)
                    }

                    else -> {
                        imgCard = null
                        hideChars + numberCard.substring(15, 19)
                    }
                }

            } catch (ex: Exception) {

            }

            imgCard?.let {
                payment_method_img?.setImageResource(it)
            }

            number_card?.text = numberCard

            if (firstDigit == 'g'.minus('0')) {
                payment_method_img?.setImageResource(R.drawable.ic_google_pay)
                number_card?.text = ""
            }
        }

        selected_payment_method?.visibility = View.VISIBLE
        payment_method?.visibility = View.GONE

        cardsBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        paymentsListAdapter.setBankCard(bankCard)

        //change btn enabled in case completed detail info
        isDataComplete()
    }


    override fun removeTickSelected() {
        val childCount = payments_list?.childCount

        childCount?.let {
            for (i in 0 until childCount) {
                val holder = payments_list?.getChildViewHolder(payments_list.getChildAt(i))
                val tick = holder?.itemView?.findViewById<ImageView>(R.id.tick)
                tick?.setImageResource(R.drawable.ic_tick)
            }
        }
    }


    override fun commentEditStart() {
        commentBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        comment_text?.let {
            if (!comment_text.isFocused) {
                comment_text?.requestFocus()
                //set a little timer to open keyboard
                Handler().postDelayed({
                    val activity = activity as? MapCreateRegularRide
                    activity?.let {
                        Keyboard.showKeyboard(activity)
                    }
                }, 200)
            }
        }
    }


    override fun setComment(comment: String) {
        comment_min_text?.text = comment
        comment_text?.setText(comment)
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


    override fun getDays(): BooleanArray {
        val arrSelected = BooleanArray(7)

        val daysStr = select_date?.text?.toString()?.trim()

        if (daysStr != null) {
            val arrDays = daysStr.split(" ")
            arrDays.forEach {
                when (it) {
                    getString(R.string.mondayC) -> arrSelected[0] = true
                    getString(R.string.tuesdayC) -> arrSelected[1] = true
                    getString(R.string.wednesdayC) -> arrSelected[2] = true
                    getString(R.string.thursdayC) -> arrSelected[3] = true
                    getString(R.string.fridayC) -> arrSelected[4] = true
                    getString(R.string.saturdayC) -> arrSelected[5] = true
                    getString(R.string.sundayC) -> arrSelected[6] = true
                }
            }
        }

        return arrSelected
    }


    override fun getTime(): Long? {
        var resultTime: Long? = null
        val stringTime = select_time?.text?.toString()
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        if (stringTime != null) {
            try {
                val date = format.parse(stringTime)
                if (date != null) {
                    val timeZone = GregorianCalendar().timeZone
                    val mGMTOffset =
                        timeZone.rawOffset + if (timeZone.inDaylightTime(Date())) timeZone.dstSavings else 0

                    resultTime = date.time.plus(mGMTOffset)
                }
            } catch (e: ParseException) {

            }
        }

        return resultTime
    }


    override fun onClickItem(address: Address, isFromMapSearch: Boolean) {
        if (isFromMapSearch) {
            from_adr?.setText(address.address)
            from_address?.text = address.address
            from_adr?.setSelection(from_adr.text.length)
            Coordinate.fromAdr = address
        } else {
            to_adr?.setText(address.address)
            to_address?.text = address.address
            to_adr?.setSelection(to_adr.text.length)
            Coordinate.toAdr = address
        }

        isDataComplete()
    }


    private fun changeBtnEnable(isEnable: Boolean) {
        if (isEnable) {
            save_ride?.isClickable = true
            save_ride?.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            save_ride?.isClickable = false
            save_ride?.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun getPaymentsAdapter(): PaymentsListAdapter = paymentsListAdapter


    override fun getAddressesAdapter(): AddressesListAdapter = addressesListAdapter


    override fun getNavHost(): NavController? = null


    override fun getUserLocation(): Point? {
        return userPoint?.let { it() }
    }


    override fun getMap(): MapView? = mapView()


    override fun moveCamera(point: Point) = moveMapCamera(point)


    private fun showMapMarker() {
        center_position?.visibility = View.VISIBLE
        center_position?.alpha = 1.0f
        shadow_marker?.visibility = View.VISIBLE
    }


    override fun hideMapMarker() {
        center_position?.visibility = View.GONE
        shadow_marker?.visibility = View.GONE
    }


    override fun showRouteBtn() {
        show_my_position_c?.visibility = View.GONE
        show_route?.visibility = View.VISIBLE
    }


    override fun hideRouteBtn() {
        show_my_position_c?.visibility = View.VISIBLE
        show_route?.visibility = View.GONE
    }


    override fun hideKeyboard() {
        val activity = activity as? MapCreateRegularRide
        activity?.let {
            Keyboard.hideKeyboard(activity, view)
        }
    }


    override fun onObjectUpdate() {
        createRegularDrivePresenter.onObjectUpdate()
    }


    override fun showNotification(text: String) {
        (activity as? MapCreateRegularRide)?.showNotification(text)
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                save_ride?.text = ""
                save_ride?.isClickable = false
                save_ride?.isFocusable = false
                progress_bar_btn?.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                save_ride?.text = getString(R.string.saveC)
                save_ride?.isClickable = true
                save_ride?.isFocusable = true
                progress_bar_btn?.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }


    private fun clearInfoData() {
        hideRouteBtn()

        createRegularDrivePresenter.removeRoute()

        resetPrice()
    }


    override fun onBackPressed(): Boolean {
        return if (cardsBottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED
            || commentBottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED
            || dateBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || daysBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || timeBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
            || addressesBottomSheet?.state != BottomSheetBehavior.STATE_COLLAPSED
        ) {
            //hide all bottom sheet
            hideAllBottomSheet()

            false
        } else {
            if (address_map_marker_layout?.visibility == View.VISIBLE) {
                val isFrom = createRegularDrivePresenter.instance().isFromMapSearch
                if (isFrom) {
                    Coordinate.fromAdr = null
                } else {
                    Coordinate.toAdr = null
                }
                removeAddressesView(isFrom)

                createRegularDrivePresenter.instance().isFromMapSearch = true
                showStartUI(true)
                false
            } else {
                ActiveRide.activeRide = null
                onDestroy()
                true
            }
        }
    }


    override fun finishActivity() {
        (activity as? MapCreateRegularRide)?.setResult(RESULT_OK)
        (activity as? MapCreateRegularRide)?.finish()
    }


    private fun getDP(dp: Int): Int? {
        var returnDP: Int? = null
        context?.resources?.displayMetrics?.density?.let {
            returnDP = ((dp * it).toInt())
        }

        return returnDP
    }


    override fun getMyCityCall(): ParentHandler<Address>? = myCityCallback


    override fun onDestroy() {
        super.onDestroy()
        createRegularDrivePresenter.onDestroy()
    }


}