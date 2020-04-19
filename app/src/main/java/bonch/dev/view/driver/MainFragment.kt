package bonch.dev.view.driver


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.utils.Constants.CREATE_RIDE_VIEW
import bonch.dev.utils.Constants.PROFILE_VIEW
import bonch.dev.utils.Constants.REGULAR_DRIVING_VIEW
import bonch.dev.view.driver.getpassanger.OrdersView
import bonch.dev.view.driver.rating.RatingView
import bonch.dev.view.passanger.getdriver.CreateRideView
import bonch.dev.view.passanger.profile.ProfileView
import bonch.dev.view.passanger.regulardrive.RegularDriveView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private var ordersView: OrdersView? = null
    private var ratingView: RatingView? = null
    private var profile: ProfileView? = null

    private var active: Fragment? = null
    private var fm: FragmentManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.main_driver_fragment, container, false)

        val navView: BottomNavigationView = root.findViewById(R.id.nav_view)

        initialize()

        active = ordersView
        navView.selectedItemId = R.id.get_driver

        fm!!.beginTransaction().add(R.id.nav_host_fragment, profile!!, PROFILE_VIEW.toString())
            .hide(profile!!).commit()
        fm!!.beginTransaction()
            .add(R.id.nav_host_fragment, ratingView!!, REGULAR_DRIVING_VIEW.toString())
            .hide(ratingView!!).commit()
        fm!!.beginTransaction()
            .add(R.id.nav_host_fragment, ordersView!!, CREATE_RIDE_VIEW.toString()).commit()


        navView.setOnNavigationItemSelectedListener(null)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        return root
    }


    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.orders -> {
                    fm!!.beginTransaction().hide(active!!).show(ordersView!!).commit()
                    item.isChecked = true
                    active = ordersView
                }

                R.id.rating -> {
                    fm!!.beginTransaction().hide(active!!).show(ratingView!!).commit()
                    item.isChecked = true
                    active = ratingView
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

        if (ordersView == null) {
            ordersView = OrdersView()
        }

        if (ratingView == null) {
            ratingView = RatingView()
        }

        if (profile == null) {
            profile = ProfileView()
        }
    }

}