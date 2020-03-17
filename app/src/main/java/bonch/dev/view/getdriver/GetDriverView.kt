package bonch.dev.view.getdriver

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.R
import bonch.dev.model.getdriver.pojo.Driver
import bonch.dev.presenter.getdriver.GetDriverPresenter
import bonch.dev.presenter.getdriver.adapters.DriversListAdapter
import bonch.dev.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.create_ride_layout.view.*
import kotlinx.android.synthetic.main.driver_info_layout.view.*
import kotlinx.android.synthetic.main.driver_info_layout.view.cancel_ride
import kotlinx.android.synthetic.main.driver_info_layout.view.reason_btn
import kotlinx.android.synthetic.main.driver_info_layout.view.reasons_bottom_sheet
import kotlinx.android.synthetic.main.get_driver_layout.view.*

class GetDriverView : Fragment(), UserLocationObjectListener, CameraListener {

    var mapView: MapView? = null
    var driversListAdapter: DriversListAdapter? = null
    var cancelBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var userLocationLayer: UserLocationLayer? = null
    private var getDriverPresenter: GetDriverPresenter? = null


    init {
        if (getDriverPresenter == null) {
            getDriverPresenter = GetDriverPresenter(this)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init map
        MapKitFactory.setApiKey(Constants.API_KEY)
        MapKitFactory.initialize(context)
        SearchFactory.initialize(context)
        DirectionsFactory.initialize(context)

        val root = inflater.inflate(R.layout.get_driver_fragment, container, false)
        mapView = root.findViewById(R.id.map) as MapView

        setUserLocation()

        mapView!!.map.addCameraListener(this)
        mapView!!.map.isRotateGesturesEnabled = false

        getDriverPresenter?.receiveUserData(arguments, root)

        setBottomSheet(root)

        setListeners(root)

        initialize(root)


        return root
    }


    override fun onObjectRemoved(view: UserLocationView) {}

    //add user location icon
    override fun onObjectAdded(userLocationView: UserLocationView) {
        val pinIcon = userLocationView.pin.useCompositeIcon()
        //TODO change to svg
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context,
                R.drawable.ic_user_mark,
                true
            )
        )

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
            ImageProvider.fromResource(context, R.drawable.ic_user_mark),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
        )

        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
    }


    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}


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


    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateSource,
        p3: Boolean
    ) {
        if (p3) {
            userLocationLayer?.resetAnchor()
        }
    }


    private fun setListeners(root: View) {
        val cancelRideBtn = root.cancel_ride
        val cancelRideBtnDone = root.reason_btn

        cancelRideBtn.setOnClickListener {
            getDriverPresenter?.getCancelReason()
        }

        cancelRideBtnDone.setOnClickListener {
            getDriverPresenter?.cancelDone()
        }
    }


    private fun setBottomSheet(root: View) {
        cancelBottomSheetBehavior = BottomSheetBehavior.from<View>(root.reasons_bottom_sheet)

        cancelBottomSheetBehavior!!.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                getDriverPresenter?.onSlideBottomSheet(slideOffset, root)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                getDriverPresenter?.onStateChangedBottomSheet(newState, root)
            }
        })
    }


    private fun initialize(root: View) {
        val list = arrayListOf<Driver>()
        for (i in 0..10){
            list.add(Driver())
        }

        driversListAdapter =
            DriversListAdapter(
                list,
                context!!,
                this
            )


        root.driver_list.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        root.driver_list.adapter = driversListAdapter
        root.driver_list.visibility = View.VISIBLE

        //createRidePresenter!!.addressesListAdapter = addressesListAdapter
    }

}
