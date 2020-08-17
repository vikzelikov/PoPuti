package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.adapters.ActiveRidesAdapter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter.ContractPresenter
import kotlinx.android.synthetic.main.active_regular_rides_fragment.*
import javax.inject.Inject

class ActiveRidesView : Fragment(), ContractView.IActiveRidesView {

    @Inject
    lateinit var activeRidesPresenter: ContractPresenter.IActiveRidesPresenter

    @Inject
    lateinit var activeRidesAdapter: ActiveRidesAdapter


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

    }


    private fun initAdapter() {
        rides_recycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = activeRidesAdapter
        }
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