package bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter

import android.os.Handler
import android.util.Log
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.interactor.passenger.regular.ride.IRegularRidesInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.ContractView
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject

class ActiveRidesPresenter : BasePresenter<ContractView.IActiveRidesView>(),
    ContractPresenter.IActiveRidesPresenter {

    @Inject
    lateinit var regularRidesInteractor: IRegularRidesInteractor

    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)
    }


    private var selectedRide: RideInfo? = null


    override fun getActiveRides() {
        var delay = 350L
        getView()?.showLoading()

        regularRidesInteractor.getActiveRides { rides, _ ->
            if (rides != null) {

                val filterRides = arrayListOf<RideInfo>()
                rides.forEach { ride ->
                    if (ride.dateInfo != null) filterRides.add(ride)
                }

                if (filterRides.isEmpty()) {
                    getView()?.showTextEmptyRides()
                } else {
                    filterRides.reverse()

                    filterRides.forEachIndexed { i, ride ->
                        Handler().postDelayed({
                            getView()?.getAdapter()?.setRide(i, ride)
                        }, delay)

                        delay += 350
                    }
                }
            } else {
                getView()?.showTextEmptyRides()
            }

            getView()?.hideLoading()
        }
    }


    override fun onClickItem(ride: RideInfo) {
        selectedRide = ride

        getView()?.onClickItem()
    }


    override fun edit() {
        val ride = selectedRide

        if (ride != null) {
            getView()?.showLoadingOpenRide()

            ActiveRide.activeRide = ride

            Handler().postDelayed({
                getView()?.startEdit()
            }, 200)
        }
    }


    override fun archive() {
        val ride = selectedRide

        if (ride != null) {
            ride.rideId?.let {
                getView()?.getAdapter()?.removeRide(it)

                getView()?.getAdapter()?.list?.let { list ->
                    if (list.isEmpty()) getView()?.showTextEmptyRides()
                }

                getView()?.archiveRide(ride)

                regularRidesInteractor.updateRideStatus(
                    StatusRide.ARCHIVE_REGULAR_RIDE,
                    it
                ) { isSuccess ->
                    if (isSuccess) {
//                        getView()?.archiveRide(ride)
                    } else {
                        getView()?.showNotification(
                            App.appComponent.getContext().getString(R.string.someError)
                        )
                    }
                }
            }
        }
    }


    override fun onRideDone(ride: RideInfo) {
        val list = getView()?.getAdapter()?.list

        val index = list?.indexOf(ride)
        if (index != null && index != -1) {
            try {
                list[index] = ride
                getView()?.getAdapter()?.notifyDataSetChanged()
            } catch (ex: IndexOutOfBoundsException) {

            }
        } else {
            getView()?.getAdapter()?.setRide(0, ride)
            getView()?.scrollTop()
        }
    }


    override fun instance() = this

}