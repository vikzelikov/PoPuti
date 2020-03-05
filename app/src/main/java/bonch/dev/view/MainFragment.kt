package bonch.dev.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.view.getdriver.GetDriverView
import bonch.dev.view.profile.ProfileView
import bonch.dev.view.regulardrive.RegularDriveView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private var regularDriving: RegularDriveView? = null
    private var getDriver: GetDriverView? = null
    private var profile: ProfileView? = null

    private var active: Fragment? = null
    private var fm: FragmentManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        val navView: BottomNavigationView = root.findViewById(R.id.nav_view)

        fm = (activity as MainActivity).supportFragmentManager

        active = getDriver
        navView.selectedItemId = R.id.get_driver

        fm!!.beginTransaction().add(R.id.nav_host_fragment, profile!!).hide(profile!!).commit()
        fm!!.beginTransaction().add(R.id.nav_host_fragment, regularDriving!!).hide(regularDriving!!).commit()
        fm!!.beginTransaction().add(R.id.nav_host_fragment, getDriver!!).commit()

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
                    fm!!.beginTransaction().hide(active!!).show(getDriver!!).commit()
                    item.isChecked = true
                    active = getDriver
                }

                R.id.profile -> {
                    fm!!.beginTransaction().hide(active!!).show(profile!!).commit()
                    item.isChecked = true
                    active = profile
                }
            }

            false
        }


    init {
        if (regularDriving == null) {
            regularDriving = RegularDriveView()
        }

        if (getDriver == null) {
            getDriver = GetDriverView()
        }

        if (profile == null) {
            profile = ProfileView()
        }
    }

}