package bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter

import android.content.Intent
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.ContractView
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.MapCreateRegularRide

class RegularRidesPresenter : BasePresenter<ContractView.IRegularDriveView>(),
    ContractPresenter.IRegularDrivePresenter {


    override fun createRegularDrive() {
        getView()?.getFragment()?.context?.let {
            getView()?.showLoading()

            //show detail order
            val intent = Intent(it, MapCreateRegularRide::class.java)
            getView()?.getFragment()?.startActivityForResult(intent, 1)
        }
    }


    override fun instance() = this

}