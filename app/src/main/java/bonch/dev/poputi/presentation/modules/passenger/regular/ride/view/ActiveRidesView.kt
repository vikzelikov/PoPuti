package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters.ActiveRidesAdapter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.active_regular_rides_fragment.*
import javax.inject.Inject


class ActiveRidesView : Fragment(), ContractView.IActiveRidesView {

    @Inject
    lateinit var activeRidesPresenter: ContractPresenter.IActiveRidesPresenter

    @Inject
    lateinit var activeRidesAdapter: ActiveRidesAdapter


    private lateinit var layoutManagerRides: LinearLayoutManager
    lateinit var editRegularRideBottomSheet: BottomSheetBehavior<RelativeLayout>
    lateinit var edit: TextView
    lateinit var archive: TextView
    lateinit var restore: TextView
    lateinit var delete: TextView
    lateinit var onView: View
    lateinit var progressBarOpenRide: ProgressBar

    lateinit var setArchiveRide: ParentHandler<RideInfo>
    lateinit var openActivity: ParentEmptyHandler


    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)

        activeRidesPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.active_regular_rides_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activeRidesPresenter.getActiveRides()

        setListeners()

        initAdapter()
    }


    override fun setListeners() {
        edit.setOnClickListener {
            activeRidesPresenter.edit()
        }

        archive.setOnClickListener {
            activeRidesPresenter.archive()

            editRegularRideBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    override fun onRideDone(ride: RideInfo) {
        activeRidesPresenter.onRideDone(ride)
    }


    override fun scrollTop() {
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
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
            adapter = activeRidesAdapter
        }
    }


    override fun onClickItem() {
        edit.visibility = View.VISIBLE
        archive.visibility = View.VISIBLE
        restore.visibility = View.GONE
        delete.visibility = View.GONE

        editRegularRideBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun setActiveRide(ride: RideInfo) {
        hideTextEmptyRides()

        activeRidesAdapter.setRide(0, ride)

        Handler().postDelayed({
            scrollTop()
        }, 500)
    }


    override fun archiveRide(ride: RideInfo) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                setArchiveRide(ride)
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun startEdit() {
        openActivity()
    }


    override fun getAdapter() = activeRidesAdapter


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


    override fun showLoadingOpenRide() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                edit.alpha = 1.0f
                edit.animate()
                    ?.alpha(0.0f)
                    ?.setDuration(180)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            edit.visibility = View.INVISIBLE
                        }
                    })

                archive.alpha = 1.0f
                archive.animate()
                    ?.alpha(0.0f)
                    ?.setDuration(180)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            archive.visibility = View.INVISIBLE
                        }
                    })

                progressBarOpenRide.alpha = 0.0f
                progressBarOpenRide.animate()
                    ?.alpha(1.0f)
                    ?.setDuration(150)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            progressBarOpenRide.visibility = View.VISIBLE
                        }
                    })

                onView.isClickable = false
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoadingOpenRide() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                edit.visibility = View.VISIBLE
                edit.alpha = 1f
                archive.visibility = View.VISIBLE
                archive.alpha = 1f
                progressBarOpenRide.visibility = View.GONE
                progressBarOpenRide.alpha = 0f
                onView.isClickable = true
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


    override fun onResume() {
        super.onResume()

        editRegularRideBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        hideLoadingOpenRide()
    }
}