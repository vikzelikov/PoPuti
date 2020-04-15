package bonch.dev.view.passanger.getdriver


import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.MainActivity
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.passanger.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.presenter.passanger.getdriver.CreateRidePresenter
import bonch.dev.presenter.passanger.getdriver.adapters.AddressesListAdapter
import bonch.dev.utils.Constants
import bonch.dev.utils.Constants.API_KEY
import bonch.dev.utils.Keyboard.hideKeyboard
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.image.ImageProvider.fromResource
import kotlinx.android.synthetic.main.create_ride_fragment.*
import kotlinx.android.synthetic.main.create_ride_layout.*
import kotlinx.android.synthetic.main.create_ride_layout.view.*

class CreateRideView : Fragment(), UserLocationObjectListener, CameraListener {

    var mapView: MapView? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var createRidePresenter: CreateRidePresenter? = null
    var navView: BottomNavigationView? = null
    var userLocationLayer: UserLocationLayer? = null
    private var addressesListAdapter: AddressesListAdapter? = null

    init {
        if (createRidePresenter == null) {
            createRidePresenter = CreateRidePresenter(this)
        }
    }

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

        val root = inflater.inflate(R.layout.create_ride_fragment, container, false)
        mapView = root.findViewById(R.id.map) as MapView

        initialize(root)

        mapView?.map?.addCameraListener(this)
        mapView?.map?.isRotateGesturesEnabled = false

        mapView?.map?.apply {
            val alignment = Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
            logo.setAlignment(alignment)
        }

        setBottomSheet()

        createRidePresenter?.getCashSuggest()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //access geo permission
        if (Permissions.isAccess(Constants.GEO_PERMISSION, activity as MainActivity)) {
            setUserLocation()
        } else {
            Permissions.access(Constants.GEO_PERMISSION_REQUEST, this)
        }

        createRidePresenter?.isUserCoordinate()

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Permissions.isAccess(Constants.GEO_PERMISSION, activity as MainActivity)) {
            setUserLocation()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //center screen position
    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if (p3) {
            userLocationLayer?.resetAnchor()
        }

        if ((p2 == CameraUpdateSource.GESTURES && p3) || fromAdr == null) {
            createRidePresenter?.requestGeocoder(Point(p1.target.latitude, p1.target.longitude))
        }
    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()

        //for moving
        val userMark = createRidePresenter?.getBitmap(R.drawable.ic_user_mark)
        userLocationView.arrow.setIcon(ImageProvider.fromBitmap(userMark))

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

        //for staying
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromBitmap(userMark),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
        )

        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
    }


    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}


    private fun setListeners(root: View) {
        my_pos.setOnClickListener {
            createRidePresenter?.showMyPosition()
        }

        from_cross.setOnClickListener {
            createRidePresenter?.touchCrossFrom(true)
        }

        to_cross.setOnClickListener {
            createRidePresenter?.touchCrossFrom(false)
        }

        from_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    createRidePresenter!!.requestSuggest(from_adr.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    from_cross.visibility = View.VISIBLE
                } else {
                    from_cross.visibility = View.GONE
                }
            }
        })

        to_adr.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    createRidePresenter!!.requestSuggest(to_adr.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                    to_cross.visibility = View.VISIBLE
                } else {
                    to_cross.visibility = View.GONE
                }
            }
        })

        from_adr.setOnTouchListener{ _: View, _: MotionEvent ->
            createRidePresenter?.touchAddress(true)
            false
        }

        to_adr.setOnTouchListener{ _: View, _: MotionEvent ->
            createRidePresenter?.touchAddress(false)
            false
        }

        on_map_view.setOnClickListener {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            hideKeyboard(activity!!, root)
        }

        btn_map_from.setOnClickListener {
            createRidePresenter?.touchMapBtn(true)
        }

        btn_map_to.setOnClickListener {
            createRidePresenter?.touchMapBtn(false)
        }

        address_map_marker_btn.setOnClickListener {
            createRidePresenter?.touchAddressMapMarkerBtn()
        }
    }


    private fun setUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        //set correct zoom
        mapView!!.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))

        //init user location service
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer?.let {
            it.isHeadingEnabled = false
            it.isVisible = true
            it.setObjectListener(this)
        }

        createRidePresenter?.startProcessBlockRequest()
    }


    private fun initialize(root: View) {
        addressesListAdapter =
            AddressesListAdapter(
                this,
                arrayListOf(),
                context!!
            )

        bottomSheetBehavior = BottomSheetBehavior.from<View>(root.bottom_sheet_addresses)
        (bottomSheetBehavior as MBottomSheet<*>).swipeEnabled = false

        root.addresses_list.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        root.addresses_list.adapter = addressesListAdapter

        createRidePresenter!!.addressesListAdapter = addressesListAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        createRidePresenter?.detailRideView?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }


    //set behaviour BottomSheet
    private fun setBottomSheet() {
        bottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                createRidePresenter?.onSlideBottomSheet(bottomSheet, slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                createRidePresenter?.onStateChangedBottomSheet(newState)
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


    override fun onResume() {
        super.onResume()
        setListeners(this.view!!)
    }


    //listener onBackPressed key
    fun onBackPressed(): Boolean {
        return createRidePresenter?.onBackPressed()!!
    }


    override fun onDestroy() {
        createRidePresenter?.onDestroy()
        super.onDestroy()
    }

}