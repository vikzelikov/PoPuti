package bonch.dev.presentation.modules.driver.signup.steps.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.R
import bonch.dev.presentation.interfaces.IBaseView
import bonch.dev.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.route.MainRouter
import kotlinx.android.synthetic.main.signup_docs_fragment.*

class ListDocsView : Fragment(), IBaseView {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.signup_docs_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }


    override fun setListeners() {
        start_driver_signup.setOnClickListener {
            MainRouter.showView(R.id.show_car_info_view, getNavHost(), null)
        }


        back_btn.setOnClickListener {
            (activity as? DriverSignupActivity)?.finish()
        }
    }


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {
        (activity as? DriverSignupActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? DriverSignupActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? DriverSignupActivity)?.hideLoading()
    }


    override fun getNavHost(): NavController? {
        return (activity as? DriverSignupActivity)?.navController
    }
}