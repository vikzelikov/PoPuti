package bonch.dev.presentation.base

import android.content.Intent
import androidx.navigation.get
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.entities.common.ride.ActiveRide
import bonch.dev.domain.entities.common.ride.RideInfo
import bonch.dev.domain.interactor.IBaseInteractor
import bonch.dev.presentation.interfaces.IMainActivity
import bonch.dev.presentation.interfaces.IMainPresenter
import bonch.dev.presentation.modules.common.ride.rate.view.RateRideView
import bonch.dev.presentation.modules.driver.getpassenger.view.MapOrderView
import bonch.dev.presentation.modules.driver.getpassenger.view.OrdersView
import bonch.dev.presentation.modules.passenger.getdriver.view.*
import bonch.dev.route.MainRouter
import javax.inject.Inject


class MainPresenter : BasePresenter<IMainActivity>(), IMainPresenter {

    @Inject
    lateinit var baseInteractor: IBaseInteractor


    init {
        App.appComponent.inject(this)
    }


    companion object {
        const val DETAIL_ORDER = 11
    }


    override fun navigate() {
        getView()?.showFullLoading()

        //check user login
        val accessToken = baseInteractor.getToken()
        val userId = baseInteractor.getUserId()

//        val rideInfo = RideInfo()
//        rideInfo.statusId = 3
//        rideInfo.position = "Васька"
//        rideInfo.destination = "Парнас"
//        ActiveRide.activeRide = rideInfo
//        redirectView(rideInfo)

        baseInteractor.validateAccount { isSuccess ->
            if (isSuccess) {
                if (accessToken != null && userId != -1) {
                    val rideId = baseInteractor.getRideId()
                    val ride = ActiveRide.activeRide

                    //check on active ride
                    when {
                        //there is active ride
                        ride != null -> redirectView(ride)

                        rideId != -1 -> {
                            //check with server on active ride
                            baseInteractor.getRide(rideId) { rideInfo, _ ->
                                if (rideInfo?.statusId != null) {
                                    //save ride local
                                    ActiveRide.activeRide = rideInfo

                                    //change view according to ride
                                    redirectView(rideInfo)

                                } else getView()?.hideFullLoading()
                            }
                        }

                        //not active ride
                        else -> redirectView(null)
                    }
                } else getView()?.hideFullLoading()
            } else getView()?.hideFullLoading()
        }
    }


    private fun redirectView(ride: RideInfo?) {
        //change behavior all screen in case open keyboard
        getView()?.changeInputMode()

        //redirect to full app
        if (baseInteractor.isCheckoutDriver()) {

            if (ride != null) {
                getView()?.getActivity()?.let { activity ->
                    val intent = Intent(activity.applicationContext, MapOrderView::class.java)

                    //show next step
                    getView()?.getActivity()?.startActivityForResult(intent, DETAIL_ORDER)
                }
            } else {
                showDriverView()

                getView()?.hideFullLoading()
            }

        } else {
            if (ride != null) {
                //next step
                MainRouter.showView(
                    R.id.get_driver_fragment,
                    getView()?.getNavHost(),
                    null
                )
            } else showPassengerView()

            getView()?.hideFullLoading()
        }
    }


    override fun showPassengerView() {
        getView()?.getNavHost()?.navigate(R.id.main_passenger_fragment)
    }


    override fun showDriverView() {
        getView()?.getNavHost()?.navigate(R.id.main_driver_fragment)
    }


    //todo переделать все onBackPress выглядит некрасиво
    override fun onBackPressed() {

        onBackPressedSignupPassanger()

        onBackPressedGetDriver()

        onBackPressedGetPassanger()

    }


    private fun onBackPressedSignupPassanger() {
        val nav = getView()?.getNavHost()

        if (nav != null) {
            if (nav.currentDestination == nav.graph[R.id.phone_view]) {
                getView()?.finishActivity()
            }

            if (nav.currentDestination == nav.graph[R.id.confirm_phone_view]) {
                nav.popBackStack()
            }

            if (nav.currentDestination == nav.graph[R.id.full_name_view]) {
                nav.popBackStack()
            }
        }
    }


    private fun onBackPressedGetDriver() {
        val fm = getView()?.getFM()
        val navHostFragment = fm?.findFragmentById(R.id.fragment_container)
        val fragment = navHostFragment?.childFragmentManager?.fragments?.get(0)

        val createRideView =
            fm?.findFragmentByTag(CreateRideView::class.java.simpleName) as? CreateRideView?
        val detailRideView =
            fm?.findFragmentByTag(DetailRideView::class.java.simpleName) as? DetailRideView?
        val getDriverView =
            fm?.findFragmentByTag(GetDriverView::class.java.simpleName) as? GetDriverView?
        val trackRideView =
            fm?.findFragmentByTag(TrackRideView::class.java.simpleName) as? TrackRideView?
        val rateRideView =
            fm?.findFragmentByTag(RateRideView::class.java.simpleName) as? RateRideView?


        if (createRideView?.view != null && createRideView.onBackPressed()) {
            getView()?.pressBack()
        }

        if (fragment is MapGetDriverView) {
            if (fragment.onBackPressed()) {
                getView()?.pressBack()
            }
        }

        if (detailRideView?.view != null) {
            detailRideView.onBackPressed()
        }

        if (getDriverView?.view != null) {
            getDriverView.onBackPressed()
        }

        if (trackRideView?.view != null) {
            trackRideView.onBackPressed()
        }

        if (rateRideView?.view != null) {
            rateRideView.onBackPressed()
        }
    }


    private fun onBackPressedGetPassanger() {
        val fm = getView()?.getFM()

        val ordersView = fm?.findFragmentByTag(OrdersView::class.java.simpleName) as? OrdersView?
        if (ordersView?.view != null) {
            getView()?.pressBack()
        }
    }


    override fun instance(): MainPresenter {
        return this
    }
}