package bonch.dev.view.driver.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.presenter.driver.signup.DriverSignupPresenter
import kotlinx.android.synthetic.main.signup_car_info_fragment.view.*

class CarInfoView : Fragment() {


    private var driverSignupPresenter: DriverSignupPresenter? = null


    init {
        if (driverSignupPresenter == null) {
            driverSignupPresenter = DriverSignupPresenter()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.signup_car_info_fragment, container, false)

        driverSignupPresenter?.setMovingButtonListener(root)

        setHintListener(root)

        setListeners(root)

        return root
    }


    private fun setListeners(root: View) {
        val carName = root.car_name
        val carModel = root.car_model
        val carNumber = root.car_number
        val nextBtn = root.next_btn

        nextBtn.setOnClickListener {
            if (driverSignupPresenter!!.isCarInfoEntered(root)) {
                val fm = (activity as DriverSignupActivity).supportFragmentManager
                driverSignupPresenter!!.startSettingDocs(fm)
            }
        }

        carName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                driverSignupPresenter?.isCarInfoEntered(root)
            }
        })

        carModel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                driverSignupPresenter?.isCarInfoEntered(root)
            }
        })

        carNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                driverSignupPresenter?.isCarInfoEntered(root)
            }
        })
    }


    private fun setHintListener(root: View) {
        val carName = root.car_name
        val carModel = root.car_model
        val carNumber = root.car_number

        carName.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                carName.hint = ""
            } else {
                carName.hint = getString(R.string.carName)
            }
        }

        carModel.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                carModel.hint = ""
            } else {
                carModel.hint = getString(R.string.carModel)
            }
        }

        carNumber.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                carNumber.hint = ""
            } else {
                carNumber.hint = getString(R.string.car_number)
            }
        }
    }

}