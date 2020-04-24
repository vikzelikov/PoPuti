package bonch.dev.presentation.driver.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.presentation.modules.driver.signup.carinfo.presenter.CarInfoPresenter
import kotlinx.android.synthetic.main.signup_car_info_fragment.view.*

class CarInfoView : Fragment() {

    private var carInfoPresenter: CarInfoPresenter? = null


    init {
        if (carInfoPresenter == null) {
            carInfoPresenter =
                CarInfoPresenter(
                    this
                )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.signup_car_info_fragment, container, false)

        setHintListener(root)

        setListeners(root)

        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        carInfoPresenter?.onActivityResult(resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun setListeners(root: View) {
        val carName = root.car_name
        val carModel = root.car_model
        val carNumber = root.car_number
        val nextBtn = root.next_btn

        nextBtn.setOnClickListener {
            if (carInfoPresenter!!.isCarInfoEntered(root)) {
                val fm = (activity as DriverSignupActivity).supportFragmentManager
                carInfoPresenter!!.startSettingDocs(fm)
            }
        }


        carName.setOnClickListener {
            carInfoPresenter?.showSuggest(true)
        }


        carModel.setOnClickListener {
            carInfoPresenter?.showSuggest(false)
        }


        carNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                carInfoPresenter?.isCarInfoEntered(root)
            }
        })
    }


    private fun setHintListener(root: View) {
        val carNumber = root.car_number

        carNumber.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            root.car_number_layout.isHintEnabled = hasFocus
        }
    }

}