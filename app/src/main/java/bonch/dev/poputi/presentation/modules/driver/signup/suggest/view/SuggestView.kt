package bonch.dev.poputi.presentation.modules.driver.signup.suggest.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.adapters.SuggestAdapter
import bonch.dev.poputi.presentation.modules.driver.signup.suggest.presenter.ISuggestPresenter
import kotlinx.android.synthetic.main.signup_car_info_suggest.*
import javax.inject.Inject

class SuggestView : AppCompatActivity(), ISuggestView {

    @Inject
    lateinit var suggestPresenter: ISuggestPresenter

    @Inject
    lateinit var suggestAdapter: SuggestAdapter


    private val BOOL_DATA = "BOOL_DATA"
    private val STRING_DATA = "STRING_DATA"


    init {
        SignupComponent.driverSignupComponent?.inject(this)

        suggestPresenter.instance().attachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_car_info_suggest)

        initAdapter()

        car_info_input.requestFocus()

        val isCarNameSuggest = intent.getBooleanExtra(BOOL_DATA, true)
        val carName = intent.getStringExtra(STRING_DATA)

        suggestPresenter.loadSuggest(isCarNameSuggest, carName)

        setListeners()
    }


    override fun setListeners() {
        car_info_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                suggestPresenter.filterList(car_info_input.text.toString())
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
            car_info_input.setText("")
        }


        back_btn.setOnClickListener {
            finish()
        }
    }


    private fun initAdapter() {
        recycler_suggest.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = suggestAdapter
        }
    }


    override fun suggestDone(textSuggest: String, isCarNameSuggest: Boolean) {
        val intent = Intent()
        intent.putExtra(STRING_DATA, textSuggest)
        intent.putExtra(BOOL_DATA, isCarNameSuggest)
        setResult(RESULT_OK, intent)
        finish()
    }


    override fun hideKeyboard() {
        Keyboard.hideKeyboard(this, suggest_container)
    }


    override fun showNotification(text: String) {}


    override fun showLoading() {}


    override fun hideLoading() {}


    override fun getNavHost(): NavController? {
        return null
    }


    override fun getAdapter(): SuggestAdapter {
        return suggestAdapter
    }


    override fun onDestroy() {
        suggestPresenter.instance().detachView()
        super.onDestroy()
    }

}