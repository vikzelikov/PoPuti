package bonch.dev.poputi.presentation.modules.common.profile.passenger.story.presenter

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject

class DetailStoryPresenter : BasePresenter<ContractView.IDetailStoryView>(),
    ContractPresenter.IDetailStoryPresenter {


    @Inject
    lateinit var profileInteractor: IProfileInteractor


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun onReceiveRide(ride: RideInfo) {
        getView()?.setRideInfo(ride)

        //submit route
    }


    override fun instance() = this

}