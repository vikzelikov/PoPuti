package bonch.dev.poputi.presentation.modules.common.profile.city

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import kotlinx.android.synthetic.main.add_bank_card_activity.*
import kotlinx.android.synthetic.main.select_city_activity.*
import kotlinx.android.synthetic.main.select_city_activity.back_btn
import javax.inject.Inject

class SelectCityView : AppCompatActivity(), ContractView.ISelectCityView {


    @Inject
    lateinit var presenter: ContractPresenter.ISelectCityPresenter


    @Inject
    lateinit var selectCityAdapter: SelectCityAdapter


    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null )
        setContentView(R.layout.select_city_activity)

        setListeners()

        initAdapter()

        input.requestFocus()

        presenter.loadSuggest()

    }


    private fun initAdapter() {
        recycler_suggest?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = selectCityAdapter
        }
    }


    override fun setListeners() {
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                presenter.filterList(input.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    cross.visibility = View.VISIBLE
                } else {
                    cross.visibility = View.GONE
                }
            }
        })

        cross.setOnClickListener {
            input.setText("")
        }


        back_btn.setOnClickListener {
            finish()
        }
    }


    override fun suggestDone(textSuggest: String) {
        val key = "CITY"
        val intent = Intent()
        intent.putExtra("CITY", textSuggest)
        setResult(RESULT_OK, intent)
        finish()
    }


    override fun getAdapter() = selectCityAdapter


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {
        Keyboard.hideKeyboard(this, this.rootLinearLayout)
    }


    override fun showNotification(text: String) {}


    override fun showLoading() {}


    override fun hideLoading() {}
}