package bonch.dev.poputi.presentation.modules.common.profile.driver.carinfo

import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject

class CarInfoPresenter : BasePresenter<ContractView.ICarInfoView>(),
    ContractPresenter.ICarInfoPresenter {


    @Inject
    lateinit var profileInteractor: IProfileInteractor


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun getCarInfo() {
        val p = CacheProfile.profile?.driver

        if (p != null) {
            getView()?.setCarInfo(p)
        }
    }


    override fun instance() = this

}