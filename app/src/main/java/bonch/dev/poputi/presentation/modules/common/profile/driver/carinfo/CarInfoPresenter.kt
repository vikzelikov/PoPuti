package bonch.dev.poputi.presentation.modules.common.profile.driver.carinfo

import android.os.Handler
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

            Handler().postDelayed({
                getView()?.hideLoading()
            }, 1000)

            getView()?.setCarInfo(p)

        } else {
            profileInteractor.getProfile { profile, _ ->
                val driver = profile?.driver
                if (driver != null) {

                    Handler().postDelayed({
                        getView()?.hideLoading()
                    }, 1000)

                    getView()?.setCarInfo(driver)
                }
            }
        }
    }


    override fun instance() = this

}