package bonch.dev.poputi.presentation.modules.passenger.getdriver.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.Coordinate
import bonch.dev.poputi.domain.utils.ChangeOpacity
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.base.MBottomSheet
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.interfaces.ParentMapHandler
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.AddressesAdapter
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.WrapContentLinearLayoutManager
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.android.synthetic.main.create_ride_layout.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.floor

class CreateRideView : Fragment(), ContractView.ICreateRideView {

    @Inject
    lateinit var createRidePresenter: ContractPresenter.ICreateRidePresenter

    @Inject
    lateinit var addressAdapter: AddressesAdapter

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var isBlockSelection = false
    private val shape = GradientDrawable()
    private val corners = floatArrayOf(24f, 24f, 24f, 24f, 0f, 0f, 0f, 0f)

    lateinit var moveMapCamera: ParentHandler<Point>
    lateinit var attachDetalRide: ParentEmptyHandler
    lateinit var zoomMap: ParentHandler<CameraPosition>
    lateinit var mapView: ParentMapHandler<MapView>
    var myCityCallback: ParentHandler<Address>? = null
    var userPoint: ParentMapHandler<Point?>? = null

    private var isUpMoved = true


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        createRidePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        correctMapView()

        createRidePresenter.instance().isNextStep = false

        return inflater.inflate(R.layout.create_ride_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)

        createRidePresenter.getCashSuggest()

        initialize()

        setListeners()

        setBottomSheet()

        from_adr?.setText(Coordinate.fromAdr?.address)
        Coordinate.toAdr = null

        userPoint?.let {
            Handler().postDelayed({
                createRidePresenter.requestGeocoder(it())
            }, 1000)
        }
    }


    private fun initialize() {
        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet_addresses)
        (bottomSheetBehavior as? MBottomSheet<*>)?.swipeEnabled = false

        //set correct height
        val display = (activity as? MainActivity)?.windowManager?.defaultDisplay
        val size = android.graphics.Point()
        display?.getSize(size)
        val height = size.y
        val layoutParams: ViewGroup.LayoutParams = bottom_sheet_addresses.layoutParams
        layoutParams.height = height - 300
        bottom_sheet_addresses.layoutParams = layoutParams

        addresses_list?.apply {
            layoutManager = WrapContentLinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = addressAdapter
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun setListeners() {
        my_pos.setOnClickListener {
            (activity as? MainActivity)?.let {
                Geo.showAlertEnable(it)
            }

            createRidePresenter.showMyPosition()
        }

        from_cross.setOnClickListener {
            createRidePresenter.touchCrossFrom(true)
        }

        to_cross.setOnClickListener {
            createRidePresenter.touchCrossFrom(false)
        }

        from_adr.setOnTouchListener { _: View, _: MotionEvent ->
            createRidePresenter.touchAddress(true)
            false
        }

        from_adr_box.setOnClickListener {
            createRidePresenter.touchAddress(true)
        }

        to_adr.setOnTouchListener { _: View, _: MotionEvent ->
            createRidePresenter.touchAddress(false)
            false
        }

        to_adr_box.setOnClickListener {
            createRidePresenter.touchAddress(false)
        }

        on_map_view.setOnClickListener {
            from_adr?.clearFocus()
            to_adr?.clearFocus()

            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            Handler().postDelayed({
                hideKeyboard()
            }, 250)
        }

        btn_map_from.setOnClickListener {
            from_adr?.clearFocus()

            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            Handler().postDelayed({
                hideKeyboard()
            }, 250)
        }

        btn_map_to.setOnClickListener {
            to_adr?.clearFocus()

            createRidePresenter.touchMapBtn(false)

            Handler().postDelayed({
                hideKeyboard()
            }, 250)
        }

        address_map_marker_btn.setOnClickListener {
            createRidePresenter.touchAddressMapMarkerBtn()
        }

        back_btn.setOnClickListener {
            onBackPressed()
        }

        setListenersAdr()
    }


    private fun setListenersAdr() {
        from_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED && !checkCompleteAddresses()) {
                    createRidePresenter.requestSuggest(from_adr?.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    from_cross?.visibility = View.VISIBLE
                } else {
                    from_cross?.visibility = View.GONE
                }
            }
        })

        to_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED && !checkCompleteAddresses()) {
                    createRidePresenter.requestSuggest(to_adr?.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
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


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.navController
    }


    private fun setBottomSheet() {
        center_position?.post { center_position?.alpha = 1.0f }
        my_pos?.post { my_pos?.alpha = 1.0f }

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                createRidePresenter.onSlideBottomSheet(bottomSheet, slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                createRidePresenter.onStateChangedBottomSheet(newState)
            }
        })
    }


    override fun showStartUI() {
        center_position?.setImageResource(R.drawable.ic_map_marker)
        main_addresses_layout?.visibility = View.VISIBLE
        addresses_list?.visibility = View.VISIBLE
        address_map_marker_layout?.visibility = View.GONE
        back_btn?.visibility = View.GONE
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun setAddressView(isFrom: Boolean, address: String) {
        if (isFrom) {
            from_adr?.setText(address)
        } else {
            to_adr?.setText(address)
        }

        address_map_text?.text = address
    }


    override fun getActualFocus(): Boolean? {
        return address_map_marker_layout?.isVisible
    }


    override fun removeAddressesView(isFrom: Boolean) {
        if (isFrom) {
            from_adr?.setText("")
//            not allow user to remove 'from' address
//            fromAdr = null
        } else {
            to_adr?.setText("")
        }
    }


    override fun addressesMapViewChanged() {
        main_addresses_layout?.visibility = View.GONE
        addresses_list?.visibility = View.GONE
        address_map_marker_layout?.visibility = View.VISIBLE
        back_btn?.visibility = View.VISIBLE

        address_map_text?.isSelected = true
        center_position?.setImageResource(R.drawable.ic_map_marker_black)
        circle_marker?.setImageResource(R.drawable.ic_input_marker_to)
        address_map_marker_btn?.setBackgroundResource(R.drawable.bg_btn_black)

        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        hideKeyboard()
    }


    override fun expandedBottomSheet(isFrom: Boolean) {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        from_adr?.isSingleLine = true
        to_adr?.isSingleLine = true

        if (isFrom) {
            to_adr?.let {
                if (it.isFocused) {
                    createRidePresenter.clearSuggest()
                }
            }

            if (!isBlockSelection) {
                from_adr?.requestFocus()
                from_adr?.text?.length?.let { from_adr?.setSelection(it) }

                if (from_adr_box?.visibility == View.VISIBLE) {
                    expandedBottomSheetEvent()
                }

                isBlockSelection = true
            }

            btn_map_from?.visibility = View.VISIBLE
            btn_map_to?.visibility = View.GONE
            to_cross?.visibility = View.GONE

            from_adr?.let {
                if (from_adr.text.isNotEmpty()) {
                    from_cross?.visibility = View.VISIBLE
                } else {
                    //set favourite addresses
                    createRidePresenter.getCashSuggest()
                    from_cross?.visibility = View.GONE
                }
            }
        } else {
            Coordinate.fromAdr?.address?.let { setAddressView(true, it) }

            from_adr?.let {
                if (it.isFocused) {
                    createRidePresenter.clearSuggest()
                }
            }

            if (!isBlockSelection) {
                to_adr?.requestFocus()
                to_adr?.text?.length?.let { to_adr?.setSelection(it) }

                if (to_adr_box?.visibility == View.VISIBLE) {
                    expandedBottomSheetEvent()
                }

                isBlockSelection = true
            }

            btn_map_from?.visibility = View.GONE
            btn_map_to?.visibility = View.VISIBLE
            from_cross?.visibility = View.GONE

            to_adr?.let {
                if (to_adr.text.isNotEmpty()) {
                    to_cross?.visibility = View.VISIBLE
                } else {
                    //set favourite addresses
                    createRidePresenter.getCashSuggest()
                    to_cross?.visibility = View.GONE
                }
            }
        }
    }


    private fun expandedBottomSheetEvent() {
        from_adr_box?.visibility = View.GONE
        to_adr_box?.visibility = View.GONE
    }


    override fun requestGeocoder(cameraPosition: CameraPosition, isUp: Boolean) {
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
                                createRidePresenter.requestGeocoder(
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


    override fun onObjectUpdate() {
        createRidePresenter.onObjectUpdate()
    }


    override fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float) {
        val expandedValue = floor((abs(slideOffset)) * 180).toInt()
        shape.cornerRadii = corners
        shape.setColor(Color.parseColor("#${ChangeOpacity.getOpacity(expandedValue)}FFFFFF"))
        bottomSheet.background = shape

        my_pos?.alpha = 1 - slideOffset * 3
        center_position?.alpha = 1 - slideOffset * 3
        on_map_view?.alpha = slideOffset * 0.8f
        addresses_list?.alpha = slideOffset
        bottom_sheet_arrow?.alpha = slideOffset
    }


    override fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            from_adr?.clearFocus()
            to_adr?.clearFocus()

            Handler().postDelayed({
                hideKeyboard()
            }, 250)
        }

        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            if (bottomSheetBehavior is MBottomSheet<*>) {
                (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = true

                activity?.let {
                    Keyboard.showKeyboard(it)
                }
            }
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            if (bottomSheetBehavior is MBottomSheet<*>) {
                btn_map_from?.visibility = View.GONE
                btn_map_to?.visibility = View.GONE
                from_cross?.visibility = View.GONE
                to_cross?.visibility = View.GONE
                on_map_view?.visibility = View.GONE
                from_adr_box?.visibility = View.VISIBLE
                to_adr_box?.visibility = View.VISIBLE
                from_adr?.clearFocus()
                to_adr?.clearFocus()

                from_adr?.isSingleLine = false
                to_adr?.isSingleLine = false
                isBlockSelection = false
                from_adr?.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                to_adr?.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = false

                Coordinate.fromAdr?.address?.let { setAddressView(true, it) }
            }
        } else {
            on_map_view?.visibility = View.VISIBLE
        }
    }


    override fun onClickItem(address: Address, isFromMapSearch: Boolean) {
        if (isFromMapSearch) {
            Coordinate.fromAdr = address
            from_adr?.setText(address.address)
            from_adr?.text?.length?.let { from_adr?.setSelection(it) }
        } else {
            Coordinate.toAdr = address
            to_adr?.setText(address.address)
            to_adr?.text?.length?.let { to_adr?.setSelection(it) }
        }
    }


    override fun getUserLocation(): Point? {
        return userPoint?.let { it() }
    }


    override fun getAddressesAdapter(): AddressesAdapter {
        return addressAdapter
    }


    override fun moveCamera(point: Point) {
        moveMapCamera(point)
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let {
            Keyboard.hideKeyboard(it, view)
        }
    }


    override fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true

        if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            isBackPressed = false
        }

        if (address_map_marker_layout?.visibility == View.VISIBLE) {
            val isFrom = createRidePresenter.instance().isFromMapSearch
            if (isFrom) {
                Coordinate.fromAdr = null
            } else {
                Coordinate.toAdr = null
            }
            removeAddressesView(isFrom)

            createRidePresenter.instance().isFromMapSearch = true
            isBackPressed = false
            showStartUI()
        }

        return isBackPressed
    }


    override fun showDetailRide() {
        //Out transition: (alpha from 0.5 to 0)
        create_ride_container?.alpha = 1.0f
        create_ride_container?.animate()
            ?.alpha(0f)
            ?.setDuration(150)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    //go to the next screen
                    nextFragment()
                }
            })
    }


    private fun correctMapView() {
        try {
            val layoutParams: RelativeLayout.LayoutParams =
                RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            layoutParams.setMargins(0, 0, 0, 0)
            mapView()?.layoutParams = layoutParams
        } catch (ex: Exception) {

        }
    }


    override fun getMyCityCall(): ParentHandler<Address>? = myCityCallback


    override fun nextFragment() {
        attachDetalRide()
    }


    override fun onDestroy() {
        createRidePresenter.onDestroy()
        super.onDestroy()
    }
}