package bonch.dev.poputi.presentation.modules.driver.signup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.driver.DaggerDriverSignupComponent
import bonch.dev.poputi.di.module.driver.DriverSignupModule
import bonch.dev.poputi.domain.entities.driver.signup.DriverData
import bonch.dev.poputi.domain.entities.driver.signup.SignupMainData
import bonch.dev.poputi.domain.entities.driver.signup.Step
import bonch.dev.poputi.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.poputi.presentation.modules.driver.signup.tabledocs.view.TableDocsView
import bonch.dev.poputi.route.MainRouter
import kotlinx.android.synthetic.main.driver_signup_activity.*
import javax.inject.Inject

/**
 * Host activity for Driver Signup
 * */

class DriverSignupActivity : AppCompatActivity() {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    private var handlerAnimation: Handler? = null

    private val CHECKOUT = -3

    init {
        initDI()

        SignupComponent.driverSignupComponent?.inject(this)
    }


    //first build DI component
    private fun initDI() {
        if (SignupComponent.driverSignupComponent == null) {
            SignupComponent.driverSignupComponent = DaggerDriverSignupComponent
                .builder()
                .driverSignupModule(DriverSignupModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_signup_activity)

        showLoading()

        clearData()

        navigateOnSignup()
    }


    val navController by lazy(LazyThreadSafetyMode.NONE) {
        Navigation.findNavController(this, R.id.fragment_container)
    }


    private fun navigateOnSignup() {
//        if (true) hideLoading()
//        else
        //try to get info about driver and docs
            signupInteractor.getDriver { driver, _ ->
                if (driver != null) {
                    hideLoading()

                    getDriverResponse(driver)
                } else {
                    //try to get driver with userId
                    signupInteractor.getUser { profile, _ ->
                        val driverData = profile?.driver

                        if (driverData == null) {
                            hideLoading()
                        } else {
                            if (driverData.isVerify) {
                                hideLoading()
                                showDriverUI()

                            } else {
                                val driverId = driverData.driverId
                                if (driverId != null) {
                                    signupInteractor.saveDriverID(driverId)

                                    signupInteractor.getDriver { driver, _ ->
                                        driver?.let { getDriverResponse(it) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }


    private fun getDriverResponse(driver: DriverData) {
        hideLoading()

        if (driver.isVerify) {
            showDriverUI()
        } else {
            showTableDocsView(driver)
        }
    }


    private fun showDriverUI() {
        signupInteractor.saveCheckoutDriver(true)
        signupInteractor.saveDriverAccess()

        setResult(CHECKOUT)

        finish()
    }


    private fun showTableDocsView(driver: DriverData) {
        SignupMainData.listDocs = driver.photoArray.toCollection(ArrayList())

        //show table docs
        MainRouter.showView(R.id.show_start_table_docs_view, navController, null)
    }


    private fun clearData() {
        SignupMainData.idStep = Step.USER_PHOTO
        SignupMainData.imgUri = null
        SignupMainData.driverData = null
        SignupMainData.listDocs = arrayListOf()
    }


    fun showNotification(text: String) {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                val view = general_notification
                view.text = text
                handlerAnimation?.removeCallbacksAndMessages(null)
                handlerAnimation = Handler()
                view.translationY = 0.0f
                view.alpha = 0.0f

                view.animate()
                    .setDuration(500L)
                    .translationY(100f)
                    .alpha(1.0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            handlerAnimation?.postDelayed({ hideNotifications() }, 1000)
                        }
                    })
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun onBackPressed() {
        val fragment = fragment_container?.childFragmentManager?.fragments?.get(0)

        if (fragment is TableDocsView) {
            if (fragment.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }


    private fun hideNotifications() {
        val view = general_notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
    }


    fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                on_view.alpha = 1.0f
                progress_bar_btn.visibility = View.VISIBLE
                on_view.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar_btn.visibility = View.GONE
                on_view.alpha = 1.0f
                on_view.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            //go to the next screen
                            on_view.visibility = View.GONE
                        }
                    })
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun onDestroy() {
        clearData()
        SignupComponent.driverSignupComponent = null
        super.onDestroy()
    }
}
