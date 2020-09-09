package bonch.dev.poputi.presentation.modules.common.onboarding.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.common.DaggerCommonComponent
import bonch.dev.poputi.di.module.common.CommonModule
import bonch.dev.poputi.presentation.modules.common.CommonComponent
import bonch.dev.poputi.presentation.modules.common.onboarding.presenter.IOnboardingPresenter
import kotlinx.android.synthetic.main.onboarding_activity.*
import javax.inject.Inject

class OnboardingView : AppCompatActivity(), IOnboardingView {


    @Inject
    lateinit var presenter: IOnboardingPresenter

    init {
        initDI()

        CommonComponent.commonComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (CommonComponent.commonComponent == null) {
            CommonComponent.commonComponent = DaggerCommonComponent
                .builder()
                .commonModule(CommonModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.onboarding_activity)

        presenter.instance().step = 0

        setListeners()
    }


    override fun setListeners() {
        val key = "IS_PASSENGER"
        val isForPassenger = intent.getBooleanExtra(key, false)

        presenter.nextStep(isForPassenger)

        next.setOnClickListener {
            next?.isClickable = false

            presenter.nextStep(isForPassenger)
        }
    }


    override fun setData(title: String, img: Int, step: Int) {
        if (step == 1) {
            title_onb?.text = title
            img_onb?.setImageResource(img)

            next?.isClickable = true

        } else {
            main_container?.animate()
                ?.setDuration(500L)
                ?.translationX(-700f)
                ?.alpha(0.0f)
                ?.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        title_onb?.text = title
                        img_onb?.setImageResource(img)
                        main_container?.translationX = 0f
                    }
                })

            Handler().postDelayed({
                main_container?.animate()
                    ?.setDuration(100L)
                    ?.alpha(1.0f)

                next?.isClickable = true
            }, 600)
        }
    }


    override fun finishOnboarding() {
        onBackPressed()
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {}


    override fun hideLoading() {}


    override fun onResume() {
        super.onResume()

        next?.isClickable = true
    }


    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(
            R.anim.activity_fade_in,
            R.anim.activity_fade_out
        )
    }
}