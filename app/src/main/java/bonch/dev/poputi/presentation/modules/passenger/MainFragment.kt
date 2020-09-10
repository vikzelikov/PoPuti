package bonch.dev.poputi.presentation.modules.passenger

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.*
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.domain.interactor.common.profile.ProfileInteractor
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.presentation.modules.common.profile.city.SelectCityView
import bonch.dev.poputi.presentation.modules.common.profile.menu.view.ProfileView
import bonch.dev.poputi.presentation.modules.common.ride.rate.view.RateRideView
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.DetailRideView
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.GetOffersView
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.MapCreateRideView
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.TrackRideView
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.RegularRidesView
import bonch.dev.poputi.service.passenger.PassengerRideService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.geometry.Point
import kotlinx.android.synthetic.main.main_passenger_fragment.*


class MainFragment : Fragment() {

    private var regularDriving: RegularRidesView? = null
    private var mapCreateRide: MapCreateRideView? = null
    private var profile: ProfileView? = null

    private var active: Fragment? = null
    private var fm: FragmentManager? = null

    private var detailRideView: DetailRideView? = null
    private var getOffersView: GetOffersView? = null
    private var trackRideView: TrackRideView? = null

    private var editRegularRideBottomSheet: BottomSheetBehavior<RelativeLayout>? = null

    private var isShowErrorPopup = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.main_passenger_fragment, container, false)

        val navView: BottomNavigationView = root.findViewById(R.id.nav_view)

        //init all variables
        initialize()

        active = mapCreateRide
        navView.selectedItemId = R.id.get_driver

        //pass args
        mapCreateRide?.arguments = arguments

        //set type UI
        profile?.isForPassenger = true

        addToBackStack()

        navView.setOnNavigationItemSelectedListener(null)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val profileInteractor: IProfileInteractor = ProfileInteractor()
        Geo.selectedCity = profileInteractor.getMyCity()

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)

        detectCity()

        setEditRegularRideUI()
    }


    private fun addToBackStack() {
        val regular = regularDriving
        val create = mapCreateRide
        val profile = profile

        if (profile != null) {
            fm?.beginTransaction()
                ?.add(R.id.nav_host_fragment, profile, ProfileView::class.java.simpleName)
                ?.hide(profile)
                ?.commit()
        }

        if (regular != null) {
            fm?.beginTransaction()
                ?.add(R.id.nav_host_fragment, regular, RegularRidesView::class.java.simpleName)
                ?.hide(regular)
                ?.commit()
        }

        if (create != null) {
            fm?.beginTransaction()
                ?.add(R.id.nav_host_fragment, create, MapCreateRideView::class.java.simpleName)
                ?.commit()

        }
    }


    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            val active = active
            val regular = regularDriving
            val create = mapCreateRide
            val profile = profile

            if (active != null) {
                when (item.itemId) {
                    R.id.regular_driving -> {
                        if (regular != null) {
                            fm?.beginTransaction()
                                ?.hide(active)
                                ?.show(regular)
                                ?.commit()
                            item.isChecked = true
                            this.active = regularDriving
                        }
                    }

                    R.id.get_driver -> {
                        if (create != null) {
                            fm?.beginTransaction()
                                ?.hide(active)
                                ?.show(create)
                                ?.commit()
                            item.isChecked = true
                            this.active = mapCreateRide
                        }
                    }

                    R.id.profile -> {
                        if (profile != null) {
                            fm?.beginTransaction()
                                ?.hide(active)
                                ?.show(profile)
                                ?.commit()
                            item.isChecked = true
                            this.active = profile
                        }
                    }
                }
            }

            false
        }


    private fun initialize() {
        fm = (activity as MainActivity).supportFragmentManager

        if (regularDriving == null) {
            regularDriving = RegularRidesView()
        }

        if (mapCreateRide == null) {
            mapCreateRide = MapCreateRideView()
            mapCreateRide?.navigateUser = { navigateUser() }
            mapCreateRide?.nextFragment = {
                ride_frame_container?.visibility = View.VISIBLE
                attachDetailRide()
            }
        }

        if (profile == null) {
            profile = ProfileView()
        }
    }


    private fun navigateUser() {
        val ride = ActiveRide.activeRide

        if (ride != null) {
            if (ride.statusId == StatusRide.REGULAR_RIDE.status) {
                val from = getFrom(ride)
                val to = getTo(ride)
                val price = ride.price

                if (from != null && to != null && price != null) {
                    Coordinate.fromAdr = from
                    Coordinate.toAdr = to
                    Coordinate.price = price

                    attachDetailRide()

                } else attachCreateRide()

            } else if (ride.statusId == StatusRide.SEARCH.status) {
                attachGetOffers()
            } else {
                //ride already created
                attachTrackRide()
            }
        } else {

            attachCreateRide()

            profile?.changeGeoMap = {
                it.point?.let { point ->
                    mapCreateRide?.moveCamera(Point(point.latitude, point.longitude))
                }
            }
        }
    }


    private fun attachCreateRide() {
        checkoutNavView(true)
        mapCreateRide?.attachCreateRide()
    }


    private fun attachDetailRide() {
        val fm = (activity as? MainActivity)?.supportFragmentManager
        val childFragment = DetailRideView()

        detailRideView = childFragment

        //pass callback
        childFragment.backHandler = {
            ride_frame_container?.visibility = View.GONE
            attachCreateRide()
        }
        childFragment.onCreateRideFail = { attachDetailRide() }
        childFragment.mapView = { mapCreateRide?.getMap() }
        childFragment.nextFragment = { attachGetOffers() }
        childFragment.notification = { showNotification(it) }

        fm?.beginTransaction()
            ?.setCustomAnimations(R.anim.slide_in_top, R.anim.fade_out)
            ?.replace(
                R.id.ride_frame_container,
                childFragment,
                DetailRideView::class.java.simpleName
            )
            ?.commit()

        get_offers_center_text?.post {
            get_offers_center_text?.alpha = 0.0f
        }
        checkoutNavView(false)
    }


    private fun attachGetOffers() {
        val fm = (activity as? MainActivity)?.supportFragmentManager
        val childFragment = GetOffersView()

        trackRideView = null
        getOffersView = childFragment

        registerReceivers()

        //pass callback
        childFragment.userPoint = { mapCreateRide?.getUserLocation() }
        childFragment.mapView = { mapCreateRide?.getMap() }
        childFragment.nextFragment = { attachTrackRide() }
        childFragment.cancelRide = { cancelRide() }
        childFragment.onGetOfferFail = { attachGetOffers() }
        childFragment.notification = { showNotification(it) }
        childFragment.offerText = get_offers_center_text

        mapCreateRide?.fadeMap()

        fm?.beginTransaction()
            ?.setCustomAnimations(R.anim.slide_in_top, R.anim.fade_out)
            ?.replace(
                R.id.ride_frame_container,
                childFragment,
                GetOffersView::class.java.simpleName
            )
            ?.commit()
    }


    private fun attachTrackRide() {
        val fm = (activity as? MainActivity)?.supportFragmentManager
        val childFragment = TrackRideView()

        getOffersView = null
        trackRideView = childFragment

        registerReceivers()

        //pass callback
        childFragment.mapView = { mapCreateRide?.getMap() }
        childFragment.nextFragment = { attachRateRide() }
        childFragment.cancelRide = { cancelRide() }
        childFragment.addDriverIcon = { mapCreateRide?.addDriverIcon(it) }
        childFragment.removeDriverIcon = { mapCreateRide?.removeDriverIcon() }
        childFragment.showUserIcon = { mapCreateRide?.showUserIcon() }
        childFragment.hideUserIcon = { mapCreateRide?.hideUserIcon() }

        mapCreateRide?.fadeMap()

        mapCreateRide?.getMap()?.map?.cameraPosition?.let {
            mapCreateRide?.zoomMapDistance(it)
        }

        fm?.beginTransaction()
            ?.setCustomAnimations(R.anim.slide_in_top, R.anim.fade_out)
            ?.replace(
                R.id.ride_frame_container,
                childFragment,
                TrackRideView::class.java.simpleName
            )
            ?.commit()
    }


    private fun attachRateRide() {
        val fm = (activity as? MainActivity)?.supportFragmentManager

        val childFragment = RateRideView()

        //pass callback
        childFragment.mapView = { mapCreateRide?.getMap() }
        childFragment.isForPassenger = true
        childFragment.backView = {
            trackRideView?.let {
                fm?.beginTransaction()
                    ?.remove(it)
                    ?.commit()
            }

            ride_frame_container?.visibility = View.GONE

            detailRideView?.removeRoute()

            attachCreateRide()
        }

        mapCreateRide?.fadeMap()

        mapCreateRide?.getMap()?.map?.cameraPosition?.let {
            mapCreateRide?.zoomMapDistance(it)
        }

        fm?.beginTransaction()
            ?.setCustomAnimations(R.anim.slide_in_top, R.anim.fade_out)
            ?.replace(R.id.ride_frame_container, childFragment, RateRideView::class.java.simpleName)
            ?.commit()
    }


    private fun startPassengerService() {
        val app = App.appComponent.getApp()

        //start background service
        app.startService(Intent(app.applicationContext, PassengerRideService::class.java))
    }


    private fun registerReceivers() {
        startPassengerService()
        //regestered receivers for listener data from service
        getOffersView?.registerReceivers()
        trackRideView?.registerReceivers()
    }


    private fun cancelRide() {
        attachDetailRide()
    }


    private fun checkoutNavView(isShow: Boolean) {
        if (isShow) {
            nav_view?.visibility = View.VISIBLE
            nav_view?.isClickable = true
            nav_view?.isFocusable = true
        } else {
            nav_view?.visibility = View.INVISIBLE
            nav_view?.isClickable = false
            nav_view?.isFocusable = false
        }
    }


    private fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    private fun setEditRegularRideUI() {
        editRegularRideBottomSheet =
            BottomSheetBehavior.from<RelativeLayout>(edit_regular_rides_bottom_sheet)

        editRegularRideBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0 && slideOffset < 1) {
                    on_view?.alpha = slideOffset * 0.8f
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    on_view?.visibility = View.GONE
                } else {
                    on_view?.visibility = View.VISIBLE
                }
            }
        })

        //pass links to views
        regularDriving?.editRegularRideBottomSheet =
            editRegularRideBottomSheet as BottomSheetBehavior<RelativeLayout>
        regularDriving?.edit = edit
        regularDriving?.archive = archive
        regularDriving?.restore = restore
        regularDriving?.delete = delete
        regularDriving?.onView = on_view
        regularDriving?.progressBarOpenRide = progress_bar

        on_view.setOnClickListener {
            editRegularRideBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    private fun detectCity() {
        val address = Geo.selectedCity

        if (address == null) {
            mapCreateRide?.myCityCallbackMain = { city ->
                city.address?.let {

                    isShowErrorPopup = false

                    showPopup(city)

                    yes?.setOnClickListener {
                        Geo.selectedCity = city

                        ProfileInteractor().saveMyCity(city)

                        city.address?.let { city -> profile?.setMyCity(city) }

                        hidePopup()
                    }

                    not?.setOnClickListener {
                        val intent = Intent(context, SelectCityView::class.java)
                        this.startActivityForResult(intent, Geo.SELECT_CITY)
                    }
                }

                mapCreateRide?.myCityCallbackMain = null

            }

        } else isShowErrorPopup = false

        select?.setOnClickListener {
            val intent = Intent(context, SelectCityView::class.java)
            this.startActivityForResult(intent, Geo.SELECT_CITY)
        }

        info_my_city?.setOnClickListener { }
    }


    private fun showPopup(city: Address) {
        on_view?.visibility = View.VISIBLE
        on_view?.alpha = 0.8f
        info_my_city?.visibility = View.VISIBLE
        val a =
            AnimationUtils.loadAnimation(
                info_my_city.context,
                R.anim.offer_slide_in_top
            )
        info_my_city?.startAnimation(a)

        city_text_view?.text = getString(R.string.yourCity)
            .plus(" ")
            .plus(city.address)
            .plus("?")
    }


    @Suppress("unused")
    private fun showPopupError() {
        on_view?.visibility = View.VISIBLE
        on_view?.alpha = 0.8f
        info_my_city?.visibility = View.VISIBLE
        val a =
            AnimationUtils.loadAnimation(
                info_my_city.context,
                R.anim.offer_slide_in_top
            )
        info_my_city?.startAnimation(a)

        yes?.visibility = View.GONE
        not?.visibility = View.GONE
        select?.visibility = View.VISIBLE

        city_text_view?.text = getString(R.string.notDetectYourCity)
    }


    private fun hidePopup() {
        on_view.visibility = View.GONE
        on_view.alpha = 0f

        info_my_city?.animate()
            ?.setDuration(200L)
            ?.translationY(100f)
            ?.alpha(0.0f)
            ?.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    info_my_city?.visibility = View.GONE
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Geo.SELECT_CITY && resultCode == Activity.RESULT_OK) {
            Geo.selectedCity?.let {
                it.point?.let { point ->

                    hidePopup()

                    mapCreateRide?.moveCamera(
                        Point(
                            point.latitude,
                            point.longitude
                        )
                    )
                }

                it.address?.let { city -> profile?.setMyCity(city) }
            }
        }
    }


    private fun getFrom(ride: RideInfo): Address? {
        val fromLat = ride.fromLat
        val fromLng = ride.fromLng

        val fromPoint = if (fromLat != null && fromLng != null) AddressPoint(fromLat, fromLng)
        else null

        return if (fromPoint != null) {
            val fromAdr = Address()
            fromAdr.point = fromPoint
            fromAdr.address = ride.position
            fromAdr

        } else null
    }


    private fun getTo(ride: RideInfo): Address? {
        val toLat = ride.toLat
        val toLng = ride.toLng

        val toPoint = if (toLat != null && toLng != null) AddressPoint(toLat, toLng)
        else null

        return if (toPoint != null) {
            val toAdr = Address()
            toAdr.point = toPoint
            toAdr.address = ride.destination
            toAdr
        } else null
    }
}