package bonch.dev.poputi.presentation.modules.passenger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.profile.view.ProfileView
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.MapCreateRideView
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.RegularRidesView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.main_passenger_fragment.*


class MainFragment : Fragment() {

    private var regularDriving: RegularRidesView? = null
    private var mapCreateRide: MapCreateRideView? = null
    private var profile: ProfileView? = null

    private var active: Fragment? = null
    private var fm: FragmentManager? = null

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
        mapCreateRide?.bottomNavView = navView

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
        }

        if (profile == null) {
            profile = ProfileView()
        }
    }


    private fun setEditRegularRideUI() {
        editRegularRideBottomSheet =
            BottomSheetBehavior.from<RelativeLayout>(edit_regular_rides_bottom_sheet)

        editRegularRideBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                onSlideBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                onStateChangedBottomSheet(newState)
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


    private fun onSlideBottomSheet(slideOffset: Float) {
        if (slideOffset > 0 && slideOffset < 1) {
            on_view?.alpha = slideOffset * 0.8f
        }
    }


    private fun onStateChangedBottomSheet(newState: Int) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            on_view?.visibility = View.GONE
        } else {
            on_view?.visibility = View.VISIBLE
        }
    }
}