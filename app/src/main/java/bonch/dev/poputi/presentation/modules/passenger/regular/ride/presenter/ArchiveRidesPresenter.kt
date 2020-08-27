package bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter

import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.interactor.passenger.regular.ride.IRegularRidesInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.ContractView
import javax.inject.Inject

class ArchiveRidesPresenter : BasePresenter<ContractView.IArchiveRidesView>(),
    ContractPresenter.IArchiveRidesPresenter {

    @Inject
    lateinit var regularRidesInteractor: IRegularRidesInteractor

    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)
    }


    private var selectedRide: RideInfo? = null


    override fun getArchiveRides() {
        getView()?.showLoading()

        regularRidesInteractor.getArchiveRides { rides, _ ->
            if (rides != null) {

                val filterRides = arrayListOf<RideInfo>()
                rides.forEach { ride ->
                    if (ride.dateInfo != null) filterRides.add(ride)
                }

                if (filterRides.isEmpty()) {
                    getView()?.showTextEmptyRides()
                } else {
                    filterRides.reverse()

                    getView()?.getAdapter()?.list = filterRides
                    getView()?.getAdapter()?.notifyDataSetChanged()
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


    override fun restore() {
        val ride = selectedRide

        if (ride != null) {
            ride.rideId?.let {
                getView()?.getAdapter()?.removeRide(it)

                getView()?.getAdapter()?.list?.let { list ->
                    if (list.isEmpty()) getView()?.showTextEmptyRides()
                }

                getView()?.restoreRide(ride)

                regularRidesInteractor.updateRideStatus(
                    StatusRide.REGULAR_RIDE,
                    it
                ) { isSuccess ->
                    if (isSuccess) {
//                        getView()?.restoreRide(ride)
                    } else {
                        getView()?.showNotification(
                            App.appComponent.getContext().getString(R.string.someError)
                        )
                    }
                }
            }
        }
    }


    override fun delete() {
        val ride = selectedRide

        if (ride != null) {
            ride.rideId?.let {
                getView()?.getAdapter()?.removeRide(it)

                getView()?.getAdapter()?.list?.let { list ->
                    if (list.isEmpty()) getView()?.showTextEmptyRides()
                }

                regularRidesInteractor.deleteRide(it){}
            }
        }
    }


    override fun instance() = this
}