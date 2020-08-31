package bonch.dev.poputi.presentation.modules.driver.signup.steps.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.Permissions
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData
import bonch.dev.poputi.domain.entities.driver.signup.SignupStep
import bonch.dev.poputi.domain.utils.Camera
import bonch.dev.poputi.domain.utils.Gallery
import bonch.dev.poputi.presentation.modules.driver.signup.DriverSignupActivity
import bonch.dev.poputi.presentation.modules.driver.signup.SignupComponent
import bonch.dev.poputi.presentation.modules.driver.signup.steps.presenter.ISignupStepPresenter
import kotlinx.android.synthetic.main.driver_signup_step_fragment.*
import javax.inject.Inject

class SignupStepView : Fragment(), ISignupStepView {

    @Inject
    lateinit var signupStepPresenter: ISignupStepPresenter


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
        super.onViewCreated(view, null)

        val stepData = signupStepPresenter.getStepData(SignupMainData.idStep)
        setDataStep(stepData)

        setListeners()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        signupStepPresenter.onActivityResult(this, requestCode, resultCode, data)

        enableButtons()
    }


    override fun setDataStep(stepData: SignupStep) {
        title_step_signup?.text = stepData.title
        subtitle_step_signup?.text = stepData.subtitle
        description_docs_signup?.text = stepData.descriptionDocs
        img_step_signup?.setImageResource(stepData.imgDocs)
    }


    override fun setListeners() {
        make_photo.setOnClickListener {
            signupStepPresenter.getCamera(this)
            blockButtons()
        }

        clip_photo.setOnClickListener {
            Gallery.getPhoto(this)
            blockButtons()
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


    override fun getNavHost(): NavController? {
        return (activity as? DriverSignupActivity)?.navController
    }


    override fun showNotification(text: String) {
        (activity as? DriverSignupActivity)?.showNotification(text)
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                on_view?.alpha = 0.9f
                on_view?.visibility = View.VISIBLE
                progress_bar_btn?.visibility = View.VISIBLE
                loading_text?.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar_btn?.visibility = View.GONE
                loading_text?.visibility = View.GONE
                on_view?.alpha = 0.9f
                on_view?.animate()
                    ?.alpha(0f)
                    ?.setDuration(500)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            //go to the next screen
                            on_view?.visibility = View.GONE
                        }
                    })
            }
        }

        mainHandler.post(myRunnable)
    }


    private fun blockButtons() {
        make_photo?.isClickable = false
        make_photo?.isFocusable = false

        clip_photo?.isClickable = false
        clip_photo?.isFocusable = false
    }


    private fun enableButtons() {
        make_photo?.isClickable = true
        make_photo?.isFocusable = true

        clip_photo?.isClickable = true
        clip_photo?.isFocusable = true
    }


    override fun onResume() {
        enableButtons()
        super.onResume()
    }


    override fun hideKeyboard() {}


    override fun onDestroy() {
        signupStepPresenter.instance().detachView()
        super.onDestroy()
    }

}