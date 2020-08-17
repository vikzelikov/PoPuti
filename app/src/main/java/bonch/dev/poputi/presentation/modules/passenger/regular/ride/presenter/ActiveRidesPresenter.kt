package bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter

import android.os.Handler
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.interactor.passenger.regular.ride.IRegularRidesInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.ContractView
import javax.inject.Inject

class ActiveRidesPresenter : BasePresenter<ContractView.IActiveRidesView>(),
    ContractPresenter.IActiveRidesPresenter {

    @Inject
    lateinit var regularRidesInteractor: IRegularRidesInteractor

    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)
    }


    override fun getActiveRides() {
        var delay = 500L
        getView()?.showLoading()

        regularRidesInteractor.getActiveRides { rides, _ ->
            if (rides != null) {

                val filterRides = arrayListOf<RideInfo>()
                rides.forEach { ride ->
                    if (ride.dateInfo != null) filterRides.add(ride)
                }

                filterRides.forEachIndexed { i, ride ->
                    Handler().postDelayed({
                        getView()?.getAdapter()?.setRide(i, ride)
                    }, delay)

                    delay += 500
                }
            } else {
                getView()?.showTextEmptyRides()
            }

            getView()?.hideLoading()
        }
    }


    override fun instance() = this

}