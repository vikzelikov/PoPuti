package bonch.dev.poputi.presentation.modules.common.profile.support

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import kotlinx.android.synthetic.main.change_lang_activity.*
import javax.inject.Inject

class SupportView : AppCompatActivity(), ContractView.ISupportView {


    @Inject
    lateinit var presenter: ContractPresenter.ISupportPresenter


    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.support_activity)

        setListeners()

    }


    override fun setListeners() {
        back_btn.setOnClickListener {
            finish()
        }
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {}


    override fun hideLoading() {}

}