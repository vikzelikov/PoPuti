package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.passenger.DaggerRegularDriveComponent
import bonch.dev.poputi.di.module.passenger.RegularRidesModule
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters.ViewPagerAdapter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import kotlinx.android.synthetic.main.regular_rides_fragment.*
import javax.inject.Inject


class RegularRidesView : Fragment(), ContractView.IRegularDriveView {

    @Inject
    lateinit var regularDrivePresenter: ContractPresenter.IRegularDrivePresenter


    init {
        initDI()

        RegularDriveComponent.regularDriveComponent?.inject(this)

        regularDrivePresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (RegularDriveComponent.regularDriveComponent == null) {
            RegularDriveComponent.regularDriveComponent = DaggerRegularDriveComponent
                .builder()
                .regularRidesModule(RegularRidesModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.regular_rides_fragment, container, false)

        val scrollView = root.findViewById<View>(R.id.nestedScrollView) as NestedScrollView
        scrollView.isFillViewport = true

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        setViewPager()
    }


    private fun setViewPager() {
        val adapter = ViewPagerAdapter(childFragmentManager)

        tabs.setupWithViewPager(viewPager)

        adapter.addFragment(ActiveRidesView(), getString(R.string.ACTIVE))
        adapter.addFragment(ArchiveRidesView(), getString(R.string.ARCHIVE))

        viewPager.adapter = adapter
    }


    override fun setListeners() {
        create_regular_ride.setOnClickListener {
            regularDrivePresenter.createRegularDrive()
        }
    }


    override fun getFragment() = this


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.getNavHost()
    }


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                (create_regular_ride as ImageButton).setImageResource(R.drawable.ic_plus_invisible)
                create_regular_ride.isClickable = false
                create_regular_ride.isFocusable = false
                progress_bar.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                (create_regular_ride as ImageButton).setImageResource(R.drawable.ic_plus_btn)
                create_regular_ride.isClickable = true
                create_regular_ride.isFocusable = true
                progress_bar.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun onResume() {
        super.onResume()
        hideLoading()
    }


}