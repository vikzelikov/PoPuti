package bonch.dev.view.driver.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.presenter.driver.signup.DriverSignupPresenter
import kotlinx.android.synthetic.main.check_correct_doc_fragment.view.*

class CheckPhotoView : Fragment() {

    private var driverSignupPresenter: DriverSignupPresenter? = null


    init {
        if (driverSignupPresenter == null) {
            driverSignupPresenter = DriverSignupPresenter(null)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.check_correct_doc_fragment, container, false)

        driverSignupPresenter?.receiveDataForCheck(root)

        setListeners(root)

        return root
    }


    private fun setListeners(root: View) {
        val back = root.back
        val sendPhoto = root.send_photo

        back.setOnClickListener {
            (activity as DriverSignupActivity).onBackPressed()
        }

        sendPhoto.setOnClickListener {
            val activity = activity as DriverSignupActivity
            driverSignupPresenter?.getNextStep(activity)
        }
    }

}