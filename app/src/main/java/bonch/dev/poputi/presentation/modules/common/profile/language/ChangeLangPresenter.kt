package bonch.dev.poputi.presentation.modules.common.profile.language

import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject

class ChangeLangPresenter : BasePresenter<ContractView.IChangeLangView>(),
    ContractPresenter.IChangeLangPresenter {

    @Inject
    lateinit var interactor: IProfileInteractor


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun instance() = this
}