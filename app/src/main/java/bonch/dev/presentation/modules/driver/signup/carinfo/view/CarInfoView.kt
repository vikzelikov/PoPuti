package bonch.dev.presentation.modules.driver.signup.carinfo.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.R
import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.SignupMainData
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.presentation.modules.driver.signup.SignupComponent
import bonch.dev.presentation.modules.driver.signup.carinfo.presenter.ICarInfoPresenter
import kotlinx.android.synthetic.main.signup_car_info_fragment.*
import javax.inject.Inject

class CarInfoView : Fragment(), ICarInfoView {

    @Inject
    lateinit var carInfoPresenter: ICarInfoPresenter

    private val BOOL_DATA = "BOOL_DATA"
    private val STRING_DATA = "STRING_DATA"

    init {
        SignupComponent.driverSignupComponent?.inject(this)

        carInfoPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.signup_car_info_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHintListener()

        setListeners()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val isCarNameSuggest = data.getBooleanExtra(BOOL_DATA, false)
            val textSuggest = data.getStringExtra(STRING_DATA)

            if (isCarNameSuggest) {
                car_name.text = textSuggest
                car_name.setTextColor(Color.parseColor("#000000"))

                //show next step
                car_model.text = resources.getString(R.string.carModel)
                car_model.setTextColor(Color.parseColor("#60000000"))
                car_model.visibility = View.VISIBLE
                car_number_layout.visibility = View.GONE
            } else {
                car_model.text = textSuggest
                car_model.setTextColor(Color.parseColor("#000000"))

                //show next step
                car_number_layout.visibility = View.VISIBLE
            }
        }
    }


    override fun setListeners() {
        next_btn.setOnClickListener {
            if (isCarInfoEntered()) {
                //save data
                SignupMainData.driverData = getData()
                //and next
                carInfoPresenter.startSetDocs()
            }
        }


        car_name.setOnClickListener {
            carInfoPresenter.showSuggest(this, true)
        }


        car_model.setOnClickListener {
            carInfoPresenter.showSuggest(this, false)
        }


        car_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isCarInfoEntered()
            }
        })
    }


    private fun setHintListener() {
        car_number.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            car_number_layout.isHintEnabled = hasFocus
        }
    }


    override fun getData(): DriverData {
        val car = DriverData()

        car.carName = car_name.text.toString().trim()
        car.carModel = car_model.text.toString().trim()
        car.carNumber = car_number.text.toString().trim()

        return car
    }


    private fun isCarInfoEntered(): Boolean {
        var result = false
        val carNumber = car_number.text.toString().trim()

        if (car_name.text.toString().trim().isNotEmpty() &&
            car_model.text.toString().trim().isNotEmpty() &&
            carNumber.isNotEmpty() && carNumber.length in 4..8
        ) {
            changeBtnEnable(true)
            result = true
        } else {
            changeBtnEnable(false)
        }


        return result
    }


    override fun changeBtnEnable(enable: Boolean) {
        if (enable) {
            next_btn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            next_btn.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }


    override fun hideKeyboard() {
        val activity = activity as? DriverSignupActivity
        activity?.let {
            Keyboard.hideKeyboard(activity, car_info_container)
        }
    }


    override fun getNavHost(): NavController? {
        return (activity as? DriverSignupActivity)?.navController
    }


    override fun onDestroy() {
        carInfoPresenter.instance().detachView()
        super.onDestroy()
    }

}