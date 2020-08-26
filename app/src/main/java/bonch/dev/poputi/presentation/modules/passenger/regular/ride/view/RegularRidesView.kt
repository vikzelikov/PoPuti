package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.passenger.DaggerRegularDriveComponent
import bonch.dev.poputi.di.module.passenger.RegularRidesModule
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Coordinate
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters.ViewPagerAdapter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.regular_rides_fragment.*
import javax.inject.Inject


class RegularRidesView : Fragment(), ContractView.IRegularDriveView {

    @Inject
    lateinit var regularDrivePresenter: ContractPresenter.IRegularDrivePresenter

    lateinit var editRegularRideBottomSheet: BottomSheetBehavior<RelativeLayout>
    lateinit var edit: TextView
    lateinit var archive: TextView
    lateinit var restore: TextView
    lateinit var delete: TextView
    lateinit var onView: View
    lateinit var progressBarOpenRide: ProgressBar

    private lateinit var activeRidesView: ActiveRidesView
    private lateinit var archiveRidesView: ArchiveRidesView

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

        passLinks()
    }


    private fun setViewPager() {
        val adapter = ViewPagerAdapter(childFragmentManager)

        tabs.setupWithViewPager(viewPager)

        activeRidesView = ActiveRidesView()
        archiveRidesView = ArchiveRidesView()

        adapter.addFragment(activeRidesView, getString(R.string.ACTIVE))
        adapter.addFragment(archiveRidesView, getString(R.string.ARCHIVE))

        viewPager?.adapter = adapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val ride = ActiveRide.activeRide
            if (ride != null) {
                activeRidesView.onRideDone(ride)

                ActiveRide.activeRide = null
            }
        }

        Coordinate.fromAdr = null
        Coordinate.toAdr = null
    }


    private fun passLinks() {
        activeRidesView.editRegularRideBottomSheet = editRegularRideBottomSheet
        activeRidesView.edit = edit
        activeRidesView.archive = archive
        activeRidesView.restore = restore
        activeRidesView.delete = delete
        activeRidesView.onView = onView
        activeRidesView.progressBarOpenRide = progressBarOpenRide
        activeRidesView.setArchiveRide = { setArchiveRide(it) }
        activeRidesView.openActivity = { regularDrivePresenter.openActivity(false) }

        archiveRidesView.editRegularRideBottomSheet = editRegularRideBottomSheet
        archiveRidesView.edit = edit
        archiveRidesView.archive = archive
        archiveRidesView.restore = restore
        archiveRidesView.delete = delete
        archiveRidesView.setActiveRide = { setActiveRide(it) }
    }


    override fun setActiveRide(ride: RideInfo) {
        activeRidesView.setActiveRide(ride)
    }


    override fun setArchiveRide(ride: RideInfo) {
        archiveRidesView.setArchiveRide(ride)
    }


    override fun setListeners() {
        create_regular_ride.setOnClickListener {
            regularDrivePresenter.openActivity(true)
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
                create_regular_ride?.setImageResource(R.drawable.ic_plus_invisible)
                create_regular_ride?.isClickable = false
                create_regular_ride?.isFocusable = false
                progress_bar_btn?.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                create_regular_ride?.setImageResource(R.drawable.ic_plus_btn)
                create_regular_ride?.isClickable = true
                create_regular_ride?.isFocusable = true
                progress_bar_btn?.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun onResume() {
        super.onResume()
        hideLoading()
    }


}