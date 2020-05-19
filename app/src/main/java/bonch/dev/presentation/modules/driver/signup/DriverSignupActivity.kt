package bonch.dev.presentation.modules.driver.signup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import bonch.dev.App
import bonch.dev.R
import bonch.dev.di.component.driver.DaggerDriverSignupComponent
import bonch.dev.di.module.driver.DriverSignupModule
import bonch.dev.domain.entities.driver.signup.DriverData
import bonch.dev.domain.entities.driver.signup.SignupMainData
import bonch.dev.domain.entities.driver.signup.Step
import bonch.dev.domain.interactor.driver.signup.ISignupInteractor
import bonch.dev.presentation.modules.driver.signup.tabledocs.view.TableDocsView
import bonch.dev.route.MainRouter
import kotlinx.android.synthetic.main.driver_signup_activity.*
import javax.inject.Inject

/**
 * Host activity for Driver Signup
 * */

class DriverSignupActivity : AppCompatActivity() {

    @Inject
    lateinit var signupInteractor: ISignupInteractor

    private var handlerAnimation: Handler? = null

    private val DRIVER_CREATED = "DRIVER_CREATED"
    private var isLoading = true
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

        startAnimLoading()

        navigateOnSignup()
    }


    val navController by lazy(LazyThreadSafetyMode.NONE) {
        Navigation.findNavController(this, R.id.fragment_container)
    }


    private fun navigateOnSignup() {
        //try to get info about driver and docs
        signupInteractor.getDriver { driver, _ ->
            isLoading = false

            if (driver != null) {
                //try to get driver with driverId
                if (driver.isVerify) {
                    //local save access to driver UI
                    showDriverUI()
                } else {
                    showTableDocsView(driver)
                }
            } else {
                //try to get driver with userId
                signupInteractor.getUser { profile, error ->
                    isLoading = false

                    val driverData = profile?.driver
                    if (error != null) {
                        showNotification(resources.getString(R.string.errorSystem))
                    } else if (driverData != null) {
                        if (driverData.isVerify) {
                            //local save access to driver UI
                            showDriverUI()
                        } else {
                            showTableDocsView(driverData)
                        }
                    }
                }
            }
        }
    }


    private fun showTableDocsView(driver: DriverData) {
        val bundle = Bundle()
        bundle.putBoolean(DRIVER_CREATED, true)
        SignupMainData.listDocs = driver.docsArray.toCollection(ArrayList())

        //show table docs
        MainRouter.showView(R.id.show_start_table_docs_view, navController, bundle)
    }


    private fun startAnimLoading() {
        Thread(Runnable {
            while (true) {
                val isLoading = this.isLoading
                if (!isLoading) {
                    val mainHandler = Handler(Looper.getMainLooper())
                    val myRunnable = Runnable {
                        kotlin.run {
                            hideLoading()
                        }
                    }

                    mainHandler.post(myRunnable)
                    break
                }
            }
        }).start()
    }


    private fun clearData() {
        SignupMainData.idStep = Step.USER_PHOTO
        SignupMainData.imgUri = null
        SignupMainData.driverData = null
        SignupMainData.listDocs = arrayListOf()
    }


    fun showNotification(text: String) {
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
                    handlerAnimation?.postDelayed({ hideNotifications() }, 2000)
                }
            })
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


    private fun hideLoading() {
        progress_bar.visibility = View.GONE
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


    private fun showDriverUI() {
        signupInteractor.saveCheckoutDriver(true)
        signupInteractor.saveDriverAccess()

        setResult(CHECKOUT)

        Handler().postDelayed({
            //todo remove
            finish()
        }, 3000)
    }


    override fun onDestroy() {
        clearData()
        SignupComponent.driverSignupComponent = null
        super.onDestroy()
    }
}
