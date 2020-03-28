package bonch.dev.view.getdriver


import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Coordinate
import bonch.dev.model.getdriver.pojo.Coordinate.fromAdr
import bonch.dev.model.getdriver.pojo.Coordinate.toAdr
import bonch.dev.model.getdriver.pojo.ReasonCancel.reasonID
import bonch.dev.presenter.getdriver.CreateRidePresenter
import bonch.dev.presenter.getdriver.adapters.AddressesListAdapter
import bonch.dev.utils.Constants.API_KEY
import bonch.dev.utils.Constants.REASON1
import bonch.dev.utils.Constants.REASON2
import bonch.dev.utils.Keyboard.hideKeyboard
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import kotlinx.android.synthetic.main.create_ride_fragment.*
import kotlinx.android.synthetic.main.create_ride_layout.*
import kotlinx.android.synthetic.main.create_ride_layout.view.*

class CreateRideView : Fragment(), UserLocationObjectListener, CameraListener {

    var mapView: MapView? = null
    var addressesListAdapter: AddressesListAdapter? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var createRidePresenter: CreateRidePresenter? = null
    var navView: BottomNavigationView? = null
    private var userLocationLayer: UserLocationLayer? = null

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

        setUserLocation()

        mapView!!.map.addCameraListener(this)
        mapView!!.map.isRotateGesturesEnabled = false

        setBottomSheet()

        createRidePresenter?.root = root

        if (reasonID == REASON1 || reasonID == REASON2) {
            if (fromAdr != null && toAdr != null) {
                //in case cancel ride from GetDriverView
                createRidePresenter?.addressesDone()
                reasonID = null
            }
        }

        if (fromAdr != null) {
            root.from_adr.setText(fromAdr!!.address)
        }

        return root
    }


    //center screen position
    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if ((p2 == CameraUpdateSource.GESTURES && p3) || fromAdr == null) {
            createRidePresenter?.requestGeocoder(Point(p1.target.latitude, p1.target.longitude))
        }

        if (p3) {
            userLocationLayer?.resetAnchor()
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

        my_pos.setOnClickListener {
            val userPoint = userLocationPoint()

            mapView!!.map.move(
                CameraPosition(userPoint!!, 35.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
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

        from_adr.setOnTouchListener { _, _ ->
            createRidePresenter?.touchAddress(true)

            false
        }

        to_adr.setOnTouchListener { _, _ ->
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


    fun userLocationPoint(): Point? {
        var point: Point? = null

        if (userLocationLayer?.cameraPosition() != null) {
            point = userLocationLayer!!.cameraPosition()!!.target
        }

        return point
    }


    private fun setUserLocation() {
        val mapKit = MapKitFactory.getInstance()
        mapView!!.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))

        //init user location service
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer?.let {
            it.isHeadingEnabled = false
            it.isVisible = true
            it.setObjectListener(this)
        }
    }


    private fun initialize(root: View) {
        addressesListAdapter =
            AddressesListAdapter(
                this,
                ArrayList(),
                context!!
            )

        bottomSheetBehavior = BottomSheetBehavior.from<View>(root.bottom_sheet_addresses)

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
    fun backPressed(): Boolean {
        return createRidePresenter?.backPressed()!!
    }


}