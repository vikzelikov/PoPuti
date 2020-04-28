package bonch.dev.presentation.modules.passanger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.MapView
import bonch.dev.presentation.modules.passanger.profile.view.ProfileView
import bonch.dev.presentation.modules.passanger.regulardrive.view.RegularDriveView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private var regularDriving: RegularDriveView? = null
    private var map: MapView? = null
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

        //map!!.navView = navView
        active = map
        navView.selectedItemId = R.id.get_driver

        fm!!.beginTransaction()
            .add(R.id.nav_host_fragment, profile!!, ProfileView::class.java.simpleName)
            .hide(profile!!).commit()
        fm!!.beginTransaction()
            .add(R.id.nav_host_fragment, regularDriving!!, RegularDriveView::class.java.simpleName)
            .hide(regularDriving!!).commit()
        fm!!.beginTransaction()
            .add(R.id.nav_host_fragment, map!!, MapView::class.java.simpleName).commit()


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
                    fm!!.beginTransaction().hide(active!!).show(map!!).commit()
                    item.isChecked = true
                    active = map
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
            regularDriving =
                RegularDriveView()
        }

        if (map == null) {
            map =
                MapView()
        }

        if (profile == null) {
            profile =
                ProfileView()
        }
    }

}