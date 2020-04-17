package bonch.dev.view.driver.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.presenter.driver.signup.CarInfoSuggestPresenter
import kotlinx.android.synthetic.main.signup_car_info_suggest.*

class CarInfoSuggestView : AppCompatActivity() {

    private var carInfoSuggestPresenter: CarInfoSuggestPresenter? = null

    init {
        if (carInfoSuggestPresenter == null) {
            carInfoSuggestPresenter = CarInfoSuggestPresenter(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_car_info_suggest)

        car_info_input.requestFocus()

        carInfoSuggestPresenter?.loadSuggest()

        setListeners()
    }


    private fun setListeners() {
        car_info_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                carInfoSuggestPresenter?.filterList(car_info_input.text.toString())
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


}