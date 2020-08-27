package bonch.dev.poputi.presentation.modules.driver


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.profile.menu.view.ProfileView
import bonch.dev.poputi.presentation.modules.driver.getpassenger.view.OrdersView
import bonch.dev.poputi.presentation.modules.driver.rating.view.RatingView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.main_driver_fragment.*

class MainFragment : Fragment() {

    private var ordersView: OrdersView? = null
    private var ratingView: RatingView? = null
    private var profile: ProfileView? = null

    private var active: Fragment? = null
    private var fm: FragmentManager? = null

    private var infoBottomSheet: BottomSheetBehavior<*>? = null


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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRatingUI()
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

            if (active != null) {
                when (item.itemId) {
                    R.id.orders -> {
                        if (orders != null) {
                            fm?.beginTransaction()?.hide(active)?.show(orders)?.commit()
                            item.isChecked = true
                            this.active = ordersView
                        }
                    }

                    R.id.rating -> {
                        if (rating != null) {
                            fm?.beginTransaction()?.hide(active)?.show(rating)?.commit()
                            item.isChecked = true
                            this.active = ratingView

                            ratingView?.getRating()
                        }
                    }

                    R.id.profile -> {
                        if (profile != null) {
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
            ordersView = OrdersView()
        }

        if (ratingView == null) {
            ratingView = RatingView()
        }

        if (profile == null) {
            profile = ProfileView()
        }
    }


    private fun setRatingUI() {
        infoBottomSheet = BottomSheetBehavior.from<View>(info_bottom_sheet)

        infoBottomSheet?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                on_view_bottom_sheet?.alpha = slideOffset * 0.8f
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    on_view_bottom_sheet?.visibility = View.GONE
                } else {
                    on_view_bottom_sheet?.visibility = View.VISIBLE
                }
            }
        })


        ratingView?.infoBottomSheet = infoBottomSheet as BottomSheetBehavior<*>

        on_view_bottom_sheet.setOnClickListener {
            infoBottomSheet?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

}