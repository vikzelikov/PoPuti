package bonch.dev.poputi.presentation.modules.driver


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.OrdersView
import bonch.dev.poputi.presentation.modules.driver.rating.view.RatingView
import bonch.dev.poputi.presentation.modules.common.profile.menu.view.ProfileView
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

        //set type UI
        profile?.isForPassenger = false

        addToBackStack()

        navView.setOnNavigationItemSelectedListener(null)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        return root
    }


    private fun addToBackStack() {
        val orders = ordersView
        val rating = ratingView
        val profile = profile

        if (profile != null) {
            fm?.beginTransaction()
                ?.add(R.id.nav_host_fragment, profile, ProfileView::class.java.simpleName)
                ?.hide(profile)
                ?.commit()
        }

        if (rating != null) {
            fm?.beginTransaction()
                ?.add(R.id.nav_host_fragment, rating, RatingView::class.java.simpleName)
                ?.hide(rating)
                ?.commit()
        }

        if (orders != null) {
            fm?.beginTransaction()
                ?.add(R.id.nav_host_fragment, orders, OrdersView::class.java.simpleName)
                ?.commit()

        }
    }


    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            val active = active
            val orders = ordersView
            val rating = ratingView
            val profile = profile

            if(active != null){
                when (item.itemId) {
                    R.id.orders -> {
                        if(orders != null){
                            fm?.beginTransaction()?.hide(active)?.show(orders)?.commit()
                            item.isChecked = true
                            this.active = ordersView
                        }
                    }

                    R.id.rating -> {
                        if(rating != null){
                            fm?.beginTransaction()?.hide(active)?.show(rating)?.commit()
                            item.isChecked = true
                            this.active = ratingView
                        }
                    }

                    R.id.profile -> {
                        if(profile != null){
                            fm?.beginTransaction()?.hide(active)?.show(profile)?.commit()
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

        if (ordersView == null) {
            ordersView =
                OrdersView()
        }

        if (ratingView == null) {
            ratingView = RatingView()
        }

        if (profile == null) {
            profile =
                ProfileView()
        }
    }

}