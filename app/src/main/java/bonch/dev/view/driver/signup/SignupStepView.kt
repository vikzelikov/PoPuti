package bonch.dev.view.driver.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.model.driver.signup.pojo.DocsStep
import bonch.dev.model.driver.signup.SignupMainData
import bonch.dev.presenter.driver.signup.DriverSignupPresenter
import bonch.dev.utils.Camera
import bonch.dev.utils.Constants
import bonch.dev.utils.Gallery
import kotlinx.android.synthetic.main.driver_signup_step_fragment.view.*

class SignupStepView : Fragment() {

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
        val root = inflater.inflate(R.layout.driver_signup_step_fragment, container, false)

        val stepData = driverSignupPresenter?.getNextStepDocs(SignupMainData.idStep)
        setDataStep(root, stepData!!)

        setListeners(root)

        return root
    }


    private fun setDataStep(root: View, stepData: DocsStep) {
        val titleStep = root.title_step_signup
        val subtitleStep = root.subtitle_step_signup
        val imgDocs = root.img_step_signup
        val descriptionStep = root.description_docs_signup

        titleStep.text = stepData.title
        subtitleStep.text = stepData.subtitle
        descriptionStep.text = stepData.descriptionDocs

        if(stepData.imgDocs != null){
            imgDocs.setImageResource(stepData.imgDocs!!)
        }
    }


    private fun setListeners(root: View) {
        val makePhoto = root.make_photo
        val clipPhoto = root.clip_photo

        makePhoto.setOnClickListener {
            driverSignupPresenter?.getCamera(this)
        }

        clipPhoto.setOnClickListener {
            val activity = activity as DriverSignupActivity
            Gallery.getPhoto(activity)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val activity = activity as DriverSignupActivity
        if(Permissions.isAccess(Constants.STORAGE_PERMISSION, activity)){
            SignupMainData.imgUri = Camera.getCamera(activity)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}