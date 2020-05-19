package bonch.dev.presentation.modules.passanger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.presentation.modules.common.profile.view.ProfileView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.MapCreateRideView
import bonch.dev.presentation.modules.passanger.regulardrive.view.RegularDriveView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private var regularDriving: RegularDriveView? = null
    private var mapCreateRide: MapCreateRideView? = null
    private var profile: ProfileView? = null

    private var active: Fragment? = null
    private var fm: FragmentManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.main_passanger_fragment, container, false)

        val navView: BottomNavigationView = root.findViewById(R.id.nav_view)

        //init all variables
        initialize()

        active = mapCreateRide
        navView.selectedItemId = R.id.get_driver

        //pass args
        mapCreateRide?.arguments = arguments
        mapCreateRide?.bottomNavView = navView

        //set type UI
        profile?.isForPassanger = true

        addToBackStack()

        navView.setOnNavigationItemSelectedListener(null)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        return root
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
                ?.add(R.id.nav_host_fragment, regular, RegularDriveView::class.java.simpleName)
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
            regularDriving = RegularDriveView()
        }

        if (mapCreateRide == null) {
            mapCreateRide = MapCreateRideView()
        }

        if (profile == null) {
            profile = ProfileView()
        }
    }

}