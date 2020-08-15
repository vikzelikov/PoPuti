package bonch.dev.poputi.presentation.modules.passenger.regulardrive.presenter

import android.content.Intent
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.passenger.regulardrive.view.ContractView
import bonch.dev.poputi.presentation.modules.passenger.regulardrive.view.MapCreateRegularDrive

class RegularDrivePresenter : BasePresenter<ContractView.IRegularDriveView>(),
    ContractPresenter.IRegularDrivePresenter {


    override fun createRegularDrive() {
        getView()?.getFragment()?.context?.let {
            getView()?.showLoading()

            //show detail order
            val intent = Intent(it, MapCreateRegularDrive::class.java)
            getView()?.getFragment()?.startActivityForResult(intent, 1)
        }
    }


    override fun instance() = this

}