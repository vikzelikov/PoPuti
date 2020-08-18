package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters.ArchiveRidesAdapter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.archive_regular_rides_fragment.*
import javax.inject.Inject

class ArchiveRidesView : Fragment(), ContractView.IArchiveRidesView {

    @Inject
    lateinit var archiveRidesPresenter: ContractPresenter.IArchiveRidesPresenter

    @Inject
    lateinit var archiveRidesAdapter: ArchiveRidesAdapter

    private lateinit var layoutManagerRides: LinearLayoutManager
    lateinit var editRegularRideBottomSheet: BottomSheetBehavior<RelativeLayout>
    lateinit var edit: TextView
    lateinit var archive: TextView
    lateinit var restore: TextView
    lateinit var delete: TextView

    lateinit var setActiveRide: ParentHandler<RideInfo>


    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)

        archiveRidesPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.archive_regular_rides_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        archiveRidesPresenter.getArchiveRides()

        setListeners()

        initAdapter()
    }


    override fun setListeners() {
        restore.setOnClickListener {
            archiveRidesPresenter.restore()

            editRegularRideBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        delete.setOnClickListener {
            archiveRidesPresenter.delete()

            editRegularRideBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    override fun scrollTop() {
        val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        smoothScroller.targetPosition = 0
        layoutManagerRides.startSmoothScroll(smoothScroller)
    }


    private fun initAdapter() {
        layoutManagerRides = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rides_recycler.apply {
            layoutManager = layoutManagerRides
            adapter = archiveRidesAdapter
        }
    }


    override fun onClickItem() {
        edit.visibility = View.GONE
        archive.visibility = View.GONE
        restore.visibility = View.VISIBLE
        delete.visibility = View.VISIBLE

        editRegularRideBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun setArchiveRide(ride: RideInfo) {
        hideTextEmptyRides()

        archiveRidesAdapter.setRide(0, ride)

        Handler().postDelayed({
            scrollTop()
        }, 500)
    }


    override fun restoreRide(ride: RideInfo) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                setActiveRide(ride)
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun getAdapter() = archiveRidesAdapter


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {
        (activity as? MainActivity)?.hideKeyboard()
    }


    override fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun showTextEmptyRides() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                text_empty_rides?.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideTextEmptyRides() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                text_empty_rides?.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar?.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar?.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }
}