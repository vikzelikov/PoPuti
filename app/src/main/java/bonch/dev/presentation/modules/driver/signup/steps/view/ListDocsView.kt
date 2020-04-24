package bonch.dev.presentation.driver.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.presentation.modules.driver.signup.steps.presenter.DriverSignupPresenter
import kotlinx.android.synthetic.main.signup_docs_fragment.view.*

class ListDocsView : Fragment() {

    private var driverSignupPresenter: DriverSignupPresenter? = null


    init {
        if (driverSignupPresenter == null) {
            driverSignupPresenter =
                DriverSignupPresenter(
                    null
                )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.signup_docs_fragment, container, false)

        setListeners(root)

        return root
    }


    private fun setListeners(root: View) {
        root.start_driver_signup.setOnClickListener {
            val fm = (activity as DriverSignupActivity).supportFragmentManager
            driverSignupPresenter?.startDriverSignup(fm)
        }


        root.back_btn.setOnClickListener {
            val activity = activity as DriverSignupActivity
            driverSignupPresenter?.finish(activity)
        }
    }
}