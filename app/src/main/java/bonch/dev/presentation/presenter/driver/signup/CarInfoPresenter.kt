package bonch.dev.presentation.presenter.driver.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import bonch.dev.R
import bonch.dev.utils.Constants
import bonch.dev.utils.Coordinator
import bonch.dev.presentation.driver.signup.CarInfoView
import kotlinx.android.synthetic.main.signup_car_info_fragment.*
import kotlinx.android.synthetic.main.signup_car_info_fragment.view.*


class CarInfoPresenter(private val carInfoView: CarInfoView) {


    fun startSettingDocs(fm: FragmentManager) {
        Coordinator.replaceFragment(Constants.DRIVER_SIGNUP_STEP_VIEW, null, fm)
    }


    fun showSuggest(isCarName: Boolean) {
        var carName: String? = null
        val context = carInfoView.requireContext()

        if (!isCarName) {
            carName = carInfoView.car_name.text.toString().trim()
        }

        Coordinator.showCarInfoSuggest(context, carInfoView, isCarName, carName)
    }


    fun onActivityResult(resultCode: Int, data: Intent?) {
        //MODE_PRIVATE - it is flag of suggest car info
        if (resultCode == Activity.MODE_PRIVATE && data != null) {
            val isCarNameSuggest = data.getBooleanExtra(Constants.BOOL_DATA, false)
            val textSuggest = data.getStringExtra(Constants.STRING_DATA)

            if (isCarNameSuggest) {
                carInfoView.car_name.text = textSuggest
                carInfoView.car_name.setTextColor(Color.parseColor("#000000"))

                //show next step
                carInfoView.car_model.visibility = View.VISIBLE
            } else {
                carInfoView.car_model.text = textSuggest
                carInfoView.car_model.setTextColor(Color.parseColor("#000000"))

                //show next step
                carInfoView.car_number_layout.visibility = View.VISIBLE
            }
        }
    }


    fun isCarInfoEntered(root: View): Boolean {
        var result = false

        val carName = root.car_name
        val carModel = root.car_model
        val carNumber = root.car_number
        val nextBtn = root.next_btn

        if (carName.text.toString().trim().isNotEmpty() &&
            carModel.text.toString().trim().isNotEmpty() &&
            carNumber.text.toString().trim().isNotEmpty()
        ) {
            changeBtnEnable(true, nextBtn)
            result = true
        } else {
            changeBtnEnable(false, nextBtn)
        }


        return result
    }


    private fun changeBtnEnable(enable: Boolean, nextBtn: Button) {
        if (enable) {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_blue)
        } else {
            nextBtn.setBackgroundResource(R.drawable.bg_btn_gray)
        }
    }
}