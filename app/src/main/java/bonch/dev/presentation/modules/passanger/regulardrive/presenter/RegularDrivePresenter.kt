package bonch.dev.presentation.modules.passanger.regulardrive.presenter

import android.content.Intent
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.passanger.regulardrive.view.ContractView
import bonch.dev.presentation.modules.passanger.regulardrive.view.MapCreateRegularDrive

class RegularDrivePresenter : BasePresenter<ContractView.IRegularDriveView>(),
    ContractPresenter.IRegularDrivePresenter {


    override fun createRegularDrive() {
        getView()?.getFragment()?.context?.let {
            //show detail order
            val intent = Intent(it, MapCreateRegularDrive::class.java)
            getView()?.getFragment()?.startActivityForResult(intent, 1)
        }
    }


    override fun instance(): RegularDrivePresenter {
        return this
    }

}