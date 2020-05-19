package bonch.dev.presentation.base

import androidx.navigation.get
import bonch.dev.App
import bonch.dev.R
import bonch.dev.domain.interactor.IBaseInteractor
import bonch.dev.presentation.interfaces.IMainActivity
import bonch.dev.presentation.interfaces.IMainPresenter
import bonch.dev.presentation.modules.common.rate.view.RateRideView
import bonch.dev.presentation.modules.driver.getpassanger.view.OrdersView
import bonch.dev.presentation.modules.passanger.getdriver.ride.view.*
import javax.inject.Inject


class MainPresenter : BasePresenter<IMainActivity>(), IMainPresenter {

    @Inject
    lateinit var baseInteractor: IBaseInteractor


    init {
        App.appComponent.inject(this)
    }


    override fun navigate() {
        //check user login
        val accessToken = baseInteractor.getToken()


        if (accessToken != null) {
            //TODO check with server if created ride already (redirect to TrackRideView)
            //redirect to full app

            if (baseInteractor.isCheckoutDriver()) {
                getView()?.getNavHost()?.navigate(R.id.main_driver_fragment)
            } else {
                getView()?.getNavHost()?.navigate(R.id.main_passanger_fragment)
            }
        }
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