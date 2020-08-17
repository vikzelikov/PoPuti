package bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter

import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.ContractView

class ArchiveRidesPresenter : BasePresenter<ContractView.IArchiveRidesView>(),
    ContractPresenter.IArchiveRidesPresenter {


    override fun instance() = this
}