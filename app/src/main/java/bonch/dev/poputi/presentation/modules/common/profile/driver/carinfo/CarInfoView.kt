package bonch.dev.poputi.presentation.modules.common.profile.driver.carinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.car_info_activity.*
import javax.inject.Inject

class CarInfoView : AppCompatActivity(), ContractView.ICarInfoView {


    @Inject
    lateinit var presenter: ContractPresenter.ICarInfoPresenter

    private var carNameSkeleton: SkeletonScreen? = null
    private var carModelSkeleton: SkeletonScreen? = null
    private var carNumberSkeleton: SkeletonScreen? = null

    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_info_activity)

        showLoading()

        setListeners()

        presenter.getCarInfo()

    }

    override fun setListeners() {
        back_btn.setOnClickListener {
            finish()
        }
    }


    override fun setCarInfo(driver: DriverData) {
        driver.carName?.let { car_name?.text = it }
        driver.carModel?.let { car_model?.text = it }
        driver.carNumber?.let { car_number?.text = it }
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {
        carNameSkeleton = Skeleton.bind(car_name)
            .load(R.layout.skeleton_layout)
            .show()

        carModelSkeleton = Skeleton.bind(car_model)
            .load(R.layout.skeleton_layout)
            .show()

        carNumberSkeleton = Skeleton.bind(car_number)
            .load(R.layout.skeleton_layout)
            .show()
    }


    override fun hideLoading() {
        carNameSkeleton?.hide()
        carModelSkeleton?.hide()
        carNumberSkeleton?.hide()
    }

}