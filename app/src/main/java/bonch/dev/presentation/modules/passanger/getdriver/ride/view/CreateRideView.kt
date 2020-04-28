package bonch.dev.presentation.modules.passanger.getdriver.ride.view

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
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.data.repository.passanger.getdriver.pojo.Coordinate
import bonch.dev.domain.utils.ChangeOpacity
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.base.MBottomSheet
import bonch.dev.presentation.modules.passanger.getdriver.GetDriverComponent
import bonch.dev.presentation.modules.passanger.getdriver.ride.adapters.AddressesListAdapter
import bonch.dev.presentation.modules.passanger.getdriver.ride.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.create_ride_layout.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.floor

class CreateRideView : Fragment(), ContractView.ICreateRideView {

    @Inject
    lateinit var createRidePresenter: ContractPresenter.ICreateRidePresenter

    @Inject
    lateinit var addressesListAdapter: AddressesListAdapter

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var isBlockSelection = false
    private val shape = GradientDrawable()
    private val corners = floatArrayOf(14f, 14f, 14f, 14f, 0f, 0f, 0f, 0f)


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        createRidePresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.create_ride_layout, container, false)

        createRidePresenter.getCashSuggest()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()

        setListeners()

        setBottomSheet()

        from_adr.setText(Coordinate.fromAdr?.address)
    }


    private fun initialize() {
        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottom_sheet_addresses)
        (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = false

        addresses_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = addressesListAdapter
        }
    }


    override fun setListeners() {
        my_pos.setOnClickListener {
            createRidePresenter.showMyPosition()
        }

        from_cross.setOnClickListener {
            createRidePresenter.touchCrossFrom(true)
        }

        to_cross.setOnClickListener {
            createRidePresenter.touchCrossFrom(false)
        }

        from_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    createRidePresenter.requestSuggest(from_adr.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    from_cross.visibility = View.VISIBLE
                } else {
                    from_cross.visibility = View.GONE
                }
            }
        })

        to_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    createRidePresenter.requestSuggest(to_adr.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    to_cross.visibility = View.VISIBLE
                } else {
                    to_cross.visibility = View.GONE
                }
            }
        })

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
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            Keyboard.hideKeyboard(activity as MainActivity, view)
        }

        btn_map_from.setOnClickListener {
            createRidePresenter.touchMapBtn(true)
        }

        btn_map_to.setOnClickListener {
            createRidePresenter.touchMapBtn(false)
        }

        address_map_marker_btn.setOnClickListener {
            createRidePresenter.touchAddressMapMarkerBtn()
        }
    }

    override fun getNavHost(): NavController {
        return (activity as MainActivity).navController
    }


    private fun setBottomSheet() {
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
        center_position.setImageResource(R.drawable.ic_map_marker)
        main_addresses_layout.visibility = View.VISIBLE
        addresses_list.visibility = View.VISIBLE
        address_map_marker_layout.visibility = View.GONE
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun setAddressView(isFrom: Boolean, address: String) {
        if (isFrom) {
            from_adr.setText(address)
        } else {
            to_adr.setText(address)
        }

        address_map_text.text = address
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


    override fun addressesMapViewChanged(isFrom: Boolean) {
        main_addresses_layout.visibility = View.GONE
        addresses_list.visibility = View.GONE
        address_map_marker_layout.visibility = View.VISIBLE

        if (isFrom) {
            address_map_text.isSelected = true
            center_position.setImageResource(R.drawable.ic_map_marker)
            circle_marker.setImageResource(R.drawable.ic_input_marker_from)
            address_map_marker_btn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            address_map_text.isSelected = true
            center_position.setImageResource(R.drawable.ic_map_marker_black)
            circle_marker.setImageResource(R.drawable.ic_input_marker_to)
            address_map_marker_btn.setBackgroundResource(R.drawable.bg_btn_black)
        }

        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        Keyboard.hideKeyboard(activity as MainActivity, view)
    }


    override fun expandedBottomSheet(isFrom: Boolean) {
        val activity = activity as MainActivity

        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        from_adr.isSingleLine = true
        to_adr.isSingleLine = true

        if (isFrom) {
            if (to_adr.isFocused) {
                createRidePresenter.clearSuggest()
            }

            if (!isBlockSelection) {
                from_adr.requestFocus()
                from_adr.setSelection(from_adr.text.length)

                if (from_adr_box.visibility == View.VISIBLE) {
                    Handler().postDelayed({
                        Keyboard.showKeyboard(activity)
                    }, 300)
                    from_adr_box.visibility = View.GONE
                    to_adr_box.visibility = View.GONE
                }

                isBlockSelection = true
            }

            btn_map_from.visibility = View.VISIBLE
            btn_map_to.visibility = View.GONE
            to_cross.visibility = View.GONE

            if (from_adr.text.isNotEmpty()) {
                from_cross.visibility = View.VISIBLE
            } else {
                from_cross.visibility = View.GONE
            }
        } else {
            if (from_adr.isFocused) {
                createRidePresenter.clearSuggest()
            }

            if (!isBlockSelection) {
                to_adr.requestFocus()
                to_adr.setSelection(to_adr.text.length)

                if (to_adr_box.visibility == View.VISIBLE) {
                    Handler().postDelayed({
                        Keyboard.showKeyboard(activity)
                    }, 300)
                    from_adr_box.visibility = View.GONE
                    to_adr_box.visibility = View.GONE
                }

                isBlockSelection = true
            }

            btn_map_from.visibility = View.GONE
            btn_map_to.visibility = View.VISIBLE
            from_cross.visibility = View.GONE

            if (to_adr.text.isNotEmpty()) {
                to_cross.visibility = View.VISIBLE
            } else {
                to_cross.visibility = View.GONE
            }
        }
    }


    override fun requestGeocoder(point: Point?) {
        createRidePresenter.requestGeocoder(point)
    }


    override fun onObjectUpdate() {
        createRidePresenter.onObjectUpdate()
    }


    override fun addressesDone(): Boolean {
        return createRidePresenter.addressesDone()
    }


    override fun dynamicReplaceViewChanged(showDetailRide: Boolean) {
//        if (showDetailRide) {
//            container_create_ride.visibility = View.GONE
//            container_detail_ride.visibility = View.VISIBLE
//            show_route.visibility = View.VISIBLE
//            //getView().navView?.visibility = View.GONE
//            back_btn.visibility = View.VISIBLE
//        } else {
//            container_create_ride.visibility = View.VISIBLE
//            container_detail_ride.visibility = View.GONE
//            show_route.visibility = View.GONE
//            navView?.visibility = View.VISIBLE
//            back_btn.visibility = View.GONE
//        }

        // on_map_view.visibility = View.GONE
    }


    override fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float) {
        val expandedValue = floor((abs(slideOffset)) * 180).toInt()
        shape.cornerRadii = corners
        shape.setColor(Color.parseColor("#${ChangeOpacity.getOpacity(expandedValue)}FFFFFF"))
        bottomSheet.background = shape

        my_pos.alpha = 1 - slideOffset * 3
        center_position.alpha = 1 - slideOffset * 3
        on_map_view.alpha = slideOffset * 0.8f
        addresses_list.alpha = slideOffset
        bottom_sheet_arrow.alpha = slideOffset
    }


    override fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            val activity = activity as MainActivity
            Keyboard.hideKeyboard(activity, view)
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            if (bottomSheetBehavior is MBottomSheet<*>) {
                (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = true
            }
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            if (bottomSheetBehavior is MBottomSheet<*>) {
                btn_map_from.visibility = View.GONE
                btn_map_to.visibility = View.GONE
                from_cross.visibility = View.GONE
                to_cross.visibility = View.GONE
                on_map_view.visibility = View.GONE
                from_adr_box.visibility = View.VISIBLE
                to_adr_box.visibility = View.VISIBLE
                from_adr.clearFocus()
                to_adr.clearFocus()

                from_adr.isSingleLine = false
                to_adr.isSingleLine = false
                isBlockSelection = false
                from_adr.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                to_adr.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = false
            }
        } else {
            on_map_view.visibility = View.VISIBLE
        }
    }


    override fun onBackPressed(): Boolean {
        var isBackPressed = true
        val fm = (activity as MainActivity).supportFragmentManager
        val detailRideView = fm.findFragmentByTag(DetailRideView::class.java.simpleName) as DetailRideView?

        if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            isBackPressed = false
        }

        if (address_map_marker_layout.visibility == View.VISIBLE) {
            createRidePresenter.instance().isFromMapSearch = true
            isBackPressed = false
            showStartUI()
        }


        if (detailRideView != null) {
            if (detailRideView.onBackPressed()) {
                //TODO
                //dynamic remove layout
                //dynamicReplaceView(false)

                //clear routing from map
                createRidePresenter.clearMapObjects()

                //block too often request
                createRidePresenter.startProcessBlockRequest()

                //set cash suggest again
                createRidePresenter.getCashSuggest()

                //set correct map view
                //getView()?.correctMapView()

                from_adr.setText(Coordinate.fromAdr?.address)
                to_adr.setText("")
                Coordinate.toAdr = null
            }

            isBackPressed = false
        }

        return isBackPressed
    }


    override fun getParentView(): ContractView.IMapView? {
        val fm = (activity as MainActivity).supportFragmentManager
        return fm.findFragmentByTag(MapView::class.java.simpleName) as MapView?
    }


    override fun getAddressesAdapter(): AddressesListAdapter {
        return addressesListAdapter
    }


    override fun onDestroy() {
        createRidePresenter.instance().detachView()
        createRidePresenter.onDestroy()
        super.onDestroy()
    }
}