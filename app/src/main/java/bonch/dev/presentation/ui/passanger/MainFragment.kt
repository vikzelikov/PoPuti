package bonch.dev.presentation.ui.passanger

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.utils.Constants.CREATE_RIDE_VIEW
import bonch.dev.utils.Constants.PROFILE_VIEW
import bonch.dev.utils.Constants.REGULAR_DRIVING_VIEW
import bonch.dev.presentation.ui.passanger.getdriver.CreateRideView
import bonch.dev.presentation.ui.passanger.profile.ProfileView
import bonch.dev.presentation.ui.passanger.regulardrive.RegularDriveView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private var regularDriving: RegularDriveView? = null
    private var createRide: CreateRideView? = null
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

        initialize()

        createRide!!.navView = navView
        active = createRide
        navView.selectedItemId = R.id.get_driver

        fm!!.beginTransaction().add(R.id.nav_host_fragment, profile!!, PROFILE_VIEW.toString())
            .hide(profile!!).commit()
        fm!!.beginTransaction()
            .add(R.id.nav_host_fragment, regularDriving!!, REGULAR_DRIVING_VIEW.toString())
            .hide(regularDriving!!).commit()
        fm!!.beginTransaction()
            .add(R.id.nav_host_fragment, createRide!!, CREATE_RIDE_VIEW.toString()).commit()


        navView.setOnNavigationItemSelectedListener(null)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        return root
    }


    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.regular_driving -> {
                    fm!!.beginTransaction().hide(active!!).show(regularDriving!!).commit()
                    item.isChecked = true
                    active = regularDriving
                }

                R.id.get_driver -> {
                    fm!!.beginTransaction().hide(active!!).show(createRide!!).commit()
                    item.isChecked = true
                    active = createRide
                }

                R.id.profile -> {
                    fm!!.beginTransaction().hide(active!!).show(profile!!).commit()
                    item.isChecked = true
                    active = profile
                }
            }

            false
        }


    private fun initialize() {
        fm = (activity as MainActivity).supportFragmentManager

        if (regularDriving == null) {
            regularDriving = RegularDriveView()
        }

        if (createRide == null) {
            createRide = CreateRideView()
        }

        if (profile == null) {
            profile = ProfileView()
        }
    }

}