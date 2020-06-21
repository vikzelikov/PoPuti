package bonch.dev

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import bonch.dev.domain.utils.Keyboard
import bonch.dev.presentation.interfaces.IMainActivity
import bonch.dev.presentation.interfaces.IMainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), IMainActivity {

    @Inject
    lateinit var mainPresenter: IMainPresenter

    private var handlerAnimation: Handler? = null


    init {
        App.appComponent.inject(this)

        mainPresenter.instance().attachView(this)
    }

    val navController by lazy(LazyThreadSafetyMode.NONE) {
        Navigation.findNavController(this, R.id.fragment_container)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainPresenter.navigate()
    }


    override fun hideKeyboard() {
        Keyboard.hideKeyboard(this, main_activity_container)
    }


    override fun setListeners() {}


    override fun getNavHost(): NavController? {
        return navController
    }


    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }


    override fun showNotification(text: String) {
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
                            handlerAnimation?.postDelayed({ hideNotifications() }, 2000)
                        }
                    })
            }
        }


        mainHandler.post(myRunnable)
    }


    private fun hideNotifications() {
        val view = general_notification

        view.animate()
            .setDuration(500L)
            .translationY(-100f)
            .alpha(0.0f)
    }


    override fun showLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                on_view.alpha = 0.7f
                progress_bar.visibility = View.VISIBLE
                on_view.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar.visibility = View.GONE
                on_view.alpha = 0.7f
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


    override fun showFullLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                on_view.alpha = 1f
                progress_bar.visibility = View.VISIBLE
                on_view.visibility = View.VISIBLE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun hideFullLoading() {
        val mainHandler = Handler(Looper.getMainLooper())
        val myRunnable = Runnable {
            kotlin.run {
                progress_bar.visibility = View.GONE
                on_view.visibility = View.GONE
            }
        }

        mainHandler.post(myRunnable)
    }


    override fun finishActivity() {
        finish()
    }


    override fun getFM(): FragmentManager {
        return supportFragmentManager
    }


    override fun pressBack() {
        super.onBackPressed()
    }


    override fun onBackPressed() {
        mainPresenter.onBackPressed()
    }
}
