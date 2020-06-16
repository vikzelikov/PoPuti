package bonch.dev.presentation.modules.driver.signup.steps.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.Permissions
import bonch.dev.R
import bonch.dev.domain.entities.driver.signup.SignupMainData
import bonch.dev.domain.entities.driver.signup.SignupStep
import bonch.dev.domain.utils.Camera
import bonch.dev.domain.utils.Gallery
import bonch.dev.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.presentation.modules.driver.signup.SignupComponent
import bonch.dev.presentation.modules.driver.signup.steps.presenter.ISignupStepPresenter
import kotlinx.android.synthetic.main.driver_signup_step_fragment.*
import javax.inject.Inject

class SignupStepView : Fragment(), ISignupStepView {

    @Inject
    lateinit var signupStepPresenter: ISignupStepPresenter

    private var blockHandler: Handler? = null
    private var isBlock = false


    init {
        SignupComponent.driverSignupComponent?.inject(this)

        signupStepPresenter.instance().attachView(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.driver_signup_step_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startProcessBlock()

        val stepData = signupStepPresenter.getStepData(SignupMainData.idStep)
        setDataStep(stepData)

        setListeners()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        signupStepPresenter.onActivityResult(this, requestCode, resultCode, data)
    }


    override fun setDataStep(stepData: SignupStep) {
        title_step_signup.text = stepData.title
        subtitle_step_signup.text = stepData.subtitle
        description_docs_signup.text = stepData.descriptionDocs
        img_step_signup.setImageResource(stepData.imgDocs)
    }


    override fun setListeners() {
        //TODO holder block btn

        make_photo.setOnClickListener {
            if (!isBlock) {
                signupStepPresenter.getCamera(this)
                isBlock = true
            }
        }

        clip_photo.setOnClickListener {
            if (!isBlock) {
                Gallery.getPhoto(this)
                isBlock = true
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        context?.let {
            if (Permissions.isAccess(Permissions.STORAGE_PERMISSION, it)) {
                SignupMainData.imgUri = Camera.getCamera(this).toString()
            }
        }
    }


    private fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 2500)
            }
        }, 0)
    }


    override fun getNavHost(): NavController? {
        return (activity as? DriverSignupActivity)?.navController
    }


    override fun showNotification(text: String) {
        (activity as? DriverSignupActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? DriverSignupActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? DriverSignupActivity)?.hideLoading()
    }


    override fun hideKeyboard() {}


    override fun onDestroy() {
        blockHandler?.removeCallbacksAndMessages(null)
        signupStepPresenter.instance().detachView()
        super.onDestroy()
    }

}