package bonch.dev.poputi.presentation.modules.passenger

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.entities.passenger.getdriver.ReasonCancel
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

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            profile =
                ProfileView()
        }
    }


    private fun navigateUser() {
        val ride = ActiveRide.activeRide

        if (ride != null) {
            if (ride.statusId == StatusRide.SEARCH.status) {
                attachGetOffers()
            } else {
                //ride already created
                attachTrackRide()
            }
        } else {
            nav_view?.visibility = View.VISIBLE
            mapCreateRide?.attachCreateRide()
        }
    }


    private fun attachDetailRide() {
        val fm = (activity as? MainActivity)?.supportFragmentManager
        val childFragment = DetailRideView()

        detailRideView = childFragment

        //pass callback
        childFragment.backHandler = {
            nav_view?.visibility = View.VISIBLE
            nav_view?.isClickable = true
            nav_view?.isFocusable = true
            ride_frame_container?.visibility = View.GONE
            mapCreateRide?.attachCreateRide()
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

        get_offers_center_text?.alpha = 0.0f
        checkoutNavView(false)
    }


    private fun attachGetOffers() {
        val fm = (activity as? MainActivity)?.supportFragmentManager
        val childFragment = GetOffersView()

        trackRideView = null
        getOffersView = childFragment

        registerReceivers()

        //pass callback
        childFragment.locationLayer = { mapCreateRide?.getUserLocation() }
        childFragment.mapView = { mapCreateRide?.getMap() }
        childFragment.nextFragment = { attachTrackRide() }
        childFragment.cancelRide = { cancelRide(it) }
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
        childFragment.cancelRide = { cancelRide(it) }

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

            checkoutNavView(true)

            mapCreateRide?.attachCreateRide()
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


    private fun cancelRide(reason: ReasonCancel) {
//        if (reason == ReasonCancel.DRIVER_CANCEL || reason == ReasonCancel.WAIT_LONG) {
//            attachDetailRide()
//        } else {
//            attachCreateRide()
//        }
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
}