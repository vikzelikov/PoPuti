package bonch.dev.view.getdriver


import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.MainActivity.Companion.getOpacity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.MainActivity.Companion.showKeyboard
import bonch.dev.R
import bonch.dev.presenter.getdriver.GetDriverPresenter
import bonch.dev.presenter.getdriver.adapters.AddressesListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider.fromResource
import kotlin.math.abs
import kotlin.math.floor


class GetDriverView(val navView: View) : Fragment(), UserLocationObjectListener, CameraListener {

    private val API_KEY = "6e2e73e8-4a73-42f5-9bf1-35259708af3c"

    var mapView: MapView? = null
    var getDriverPresenter: GetDriverPresenter? = null
    var addressesListAdapter: AddressesListAdapter? = null
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var userLocationLayer: UserLocationLayer? = null

    var isMapSearchGestures = false
    var isFromMapSearch = true

    lateinit var toAddress: EditText
    lateinit var fromAddress: EditText
    lateinit var addressMapText: TextView
    lateinit var getDriverLayout: CoordinatorLayout
    lateinit var detailRideLayout: RelativeLayout
    lateinit var onMapView: View
    private lateinit var myPos: ImageButton
    private lateinit var bottomSheet: RelativeLayout
    private lateinit var centerPosition: ImageView
    private lateinit var arrowBottomSheet: ImageView
    private lateinit var recyclerAddresses: RecyclerView
    private lateinit var fromMapBtn: TextView
    private lateinit var toMapBtn: TextView
    private lateinit var crossFromDelete: ImageView
    private lateinit var crossToDelete: ImageView
    private lateinit var containerAddresses: LinearLayout
    private lateinit var addressesMapMarkerLayout: LinearLayout
    private lateinit var addressMapMarkerBtn: Button
    private lateinit var circleMarker: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init map
        MapKitFactory.setApiKey(API_KEY)
        MapKitFactory.initialize(context)
        SearchFactory.initialize(context)
        DirectionsFactory.initialize(context)

        val mapKit = MapKitFactory.getInstance()
        val root = inflater.inflate(R.layout.get_driver_fragment, container, false)

        initViews(root)

        if (getDriverPresenter == null) {
            getDriverPresenter = GetDriverPresenter(this, root)
        }

        //for correct user show position
        mapView!!.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))

        //init user location service
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.isHeadingEnabled = false
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.setObjectListener(this)
        mapView!!.map.addCameraListener(this)

        mapView!!.map.isRotateGesturesEnabled = false

        setListeners(root)

        setBottomSheet(root)

        initialize()

        return root
    }


    //center screen position
    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if (getDriverPresenter != null && p2 == CameraUpdateSource.GESTURES && p3) {
            getDriverPresenter!!.requestGeocoder(Point(p1.target.latitude, p1.target.longitude))
        }

        if (p3) {
            if (userLocationLayer != null) {
                userLocationLayer!!.resetAnchor()
            }
        }
    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()
        //TODO change to svg
        userLocationView.arrow.setIcon(fromResource(context, R.drawable.ic_user_mark, true))

        userLocationLayer!!.setAnchor(
            PointF(
                (mapView!!.width * 0.5).toFloat(),
                (mapView!!.height * 0.5).toFloat()
            ),
            PointF(
                (mapView!!.width * 0.5).toFloat(),
                (mapView!!.height * 0.83).toFloat()
            )
        )

        pinIcon.setIcon(
            "pin",
            fromResource(context, R.drawable.ic_user_mark),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
        )

        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
    }


    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}


    private fun setListeners(root: View) {
        var userPoint: Point

        myPos.setOnClickListener {
            userPoint = userLocationPoint()!!
            moveCamera(userPoint)
        }


        crossFromDelete.setOnClickListener {
            fromAddress.setText("")
        }


        crossToDelete.setOnClickListener {
            toAddress.setText("")
        }


        fromAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    getDriverPresenter!!.requestSuggest(fromAddress.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    crossFromDelete.visibility = View.VISIBLE
                } else {
                    crossFromDelete.visibility = View.GONE
                }
            }
        })


        toAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    getDriverPresenter!!.requestSuggest(toAddress.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    crossToDelete.visibility = View.VISIBLE
                } else {
                    crossToDelete.visibility = View.GONE
                }
            }
        })


        (fromAddress as View).setOnTouchListener { _, _ ->
            if (addressesListAdapter != null && toAddress.isFocused) {
                addressesListAdapter!!.list.clear()
                addressesListAdapter!!.notifyDataSetChanged()
            }

            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

            if (!fromAddress.isFocused) {
                fromAddress.requestFocus()
                showKeyboard(activity!!)
            }

            fromMapBtn.visibility = View.VISIBLE
            toMapBtn.visibility = View.GONE
            crossToDelete.visibility = View.GONE

            if (fromAddress.text.isNotEmpty()) {
                crossFromDelete.visibility = View.VISIBLE
            } else {
                crossFromDelete.visibility = View.GONE
            }

            false
        }

        (toAddress as View).setOnTouchListener { _, _ ->
            if (addressesListAdapter != null && fromAddress.isFocused) {
                addressesListAdapter!!.list.clear()
                addressesListAdapter!!.notifyDataSetChanged()
            }

            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

            if (!toAddress.isFocused) {
                toAddress.requestFocus()
                showKeyboard(activity!!)
            }

            fromMapBtn.visibility = View.GONE
            toMapBtn.visibility = View.VISIBLE
            crossFromDelete.visibility = View.GONE

            if (toAddress.text.isNotEmpty()) {
                crossToDelete.visibility = View.VISIBLE
            } else {
                crossToDelete.visibility = View.GONE
            }

            false
        }


        onMapView.setOnClickListener {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            hideKeyboard(activity!!, root)
        }

        containerAddresses.setOnClickListener {}

        fromMapBtn.setOnClickListener {
            containerAddresses.visibility = View.GONE
            addressesMapMarkerLayout.visibility = View.VISIBLE

            isFromMapSearch = true
            isMapSearchGestures = true
            addressMapText.isSelected = true
            centerPosition.setImageResource(R.drawable.ic_map_marker)
            circleMarker.setImageResource(R.drawable.ic_input_marker_from)
            addressMapMarkerBtn.setBackgroundResource(R.drawable.bg_btn_blue)

            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            fromAddress.clearFocus()
            hideKeyboard(activity!!, root)
        }

        toMapBtn.setOnClickListener {
            containerAddresses.visibility = View.GONE
            addressesMapMarkerLayout.visibility = View.VISIBLE

            isFromMapSearch = false
            isMapSearchGestures = true
            addressMapText.isSelected = true
            centerPosition.setImageResource(R.drawable.ic_map_marker_black)
            circleMarker.setImageResource(R.drawable.ic_input_marker_to)
            addressMapMarkerBtn.setBackgroundResource(R.drawable.bg_btn_black)

            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            toAddress.clearFocus()
            addressMapText.text = ""
            hideKeyboard(activity!!, root)
        }

        addressMapMarkerBtn.setOnClickListener {
            if (addressesListAdapter!!.fromAdr != null && addressesListAdapter!!.toAdr != null) {
                //addresses filled
                getDriverPresenter!!.addressesDone()
            } else {
                centerPosition.setImageResource(R.drawable.ic_map_marker)
                containerAddresses.visibility = View.VISIBLE
                addressesMapMarkerLayout.visibility = View.GONE
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }


    fun userLocationPoint(): Point? {
        var point: Point? = null

        if (userLocationLayer!!.cameraPosition() != null) {
            point = userLocationLayer!!.cameraPosition()!!.target
        }

        return point
    }


    //move Map camera
    fun moveCamera(point: Point) {
        mapView!!.map.move(
            CameraPosition(point, 35.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }


    private fun initViews(root: View) {
        mapView = root.findViewById(R.id.map) as MapView
        myPos = root.findViewById(R.id.my_pos)
        fromAddress = root.findViewById(R.id.from)
        toAddress = root.findViewById(R.id.to)
        centerPosition = root.findViewById(R.id.center_position)
        arrowBottomSheet = root.findViewById(R.id.bottom_sheet_arrow)
        recyclerAddresses = root.findViewById(R.id.addresses_list)
        fromMapBtn = root.findViewById(R.id.btn_map_from)
        toMapBtn = root.findViewById(R.id.btn_map_to)
        crossFromDelete = root.findViewById(R.id.from_cross)
        crossToDelete = root.findViewById(R.id.to_cross)
        onMapView = root.findViewById(R.id.on_map_view)
        containerAddresses = root.findViewById(R.id.main_addresses_layout)
        addressesMapMarkerLayout = root.findViewById(R.id.address_map_marker_layout)
        addressMapText = root.findViewById(R.id.address_map_text)
        addressMapMarkerBtn = root.findViewById(R.id.address_map_marker_btn)
        circleMarker = root.findViewById(R.id.circle_marker)
        getDriverLayout = root.findViewById(R.id.get_driver_layout)
        detailRideLayout = root.findViewById(R.id.detail_layout)

        addressesListAdapter =
            AddressesListAdapter(
                this,
                ArrayList(),
                context!!,
                root
            )

        bottomSheet = root.findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheet)
    }


    private fun initialize() {
        recyclerAddresses.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerAddresses.adapter = addressesListAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (getDriverPresenter!!.detailRideView != null) {
            getDriverPresenter!!.detailRideView!!.onActivityResult(
                context!!,
                requestCode,
                resultCode,
                data
            )
        }
    }


    //set behaviour BottomSheet
    private fun setBottomSheet(root: View) {
        var expandedValue: Int
        val shape = GradientDrawable()
        val corners = floatArrayOf(10f, 10f, 10f, 10f, 0f, 0f, 0f, 0f)

        shape.cornerRadii = corners

        bottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    expandedValue = floor((abs(slideOffset)) * 180).toInt()
                    shape.setColor(Color.parseColor("#${getOpacity(expandedValue)}FFFFFF"))
                    bottomSheet.background = shape

                    myPos.alpha = 1 - slideOffset * 3
                    centerPosition.alpha = 1 - slideOffset * 3
                    onMapView.alpha = slideOffset * 0.8f
                    recyclerAddresses.alpha = slideOffset
                    arrowBottomSheet.alpha = slideOffset
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    hideKeyboard(activity!!, root)
                    toAddress.clearFocus()
                    fromAddress.clearFocus()
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (bottomSheetBehavior is MBottomSheet<*>) {
                        (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = true
                    }
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (bottomSheetBehavior is MBottomSheet<*>) {
                        toMapBtn.visibility = View.GONE
                        fromMapBtn.visibility = View.GONE
                        crossToDelete.visibility = View.GONE
                        crossFromDelete.visibility = View.GONE
                        onMapView.visibility = View.GONE
                        (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled =
                            false
                    }
                } else {
                    onMapView.visibility = View.VISIBLE
                }
            }

        })
    }


    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        mapView!!.onStop()
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }


    //listener onBackPressed key
    fun backPressed(): Boolean {
        var result = true

        if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            result = false
        }

        if (isMapSearchGestures) {
            centerPosition.setImageResource(R.drawable.ic_map_marker)
            containerAddresses.visibility = View.VISIBLE
            addressesMapMarkerLayout.visibility = View.GONE
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            isMapSearchGestures = false
            result = false
        }

        if (getDriverPresenter!!.detailRideView != null) {
            navView.visibility = View.VISIBLE
            detailRideLayout.visibility = View.GONE
            getDriverLayout.visibility = View.VISIBLE
            onMapView.visibility = View.GONE
            getDriverPresenter!!.clickBackGetDriver()
            getDriverPresenter!!.detailRideView = null
            toAddress.setText("")

            result = false
        }

        return result
    }


}