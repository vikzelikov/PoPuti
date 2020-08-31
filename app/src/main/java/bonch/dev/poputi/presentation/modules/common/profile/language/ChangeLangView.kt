package bonch.dev.poputi.presentation.modules.common.profile.language

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import kotlinx.android.synthetic.main.change_lang_activity.*
import javax.inject.Inject

class ChangeLangView : AppCompatActivity(), ContractView.IChangeLangView {


    @Inject
    lateinit var presenter: ContractPresenter.IChangeLangPresenter


    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.change_lang_activity)

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