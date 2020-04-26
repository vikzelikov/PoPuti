package bonch.dev

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import bonch.dev.data.repository.passanger.getdriver.pojo.DriverObject
import bonch.dev.domain.utils.Constants.PHONE_VIEW
import bonch.dev.domain.utils.Keyboard.hideKeyboard
import bonch.dev.presentation.base.MainPresenter
import bonch.dev.route.Coordinator.replaceFragment
import bonch.dev.route.passanger.signup.ISignupRouter
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {


    private var mainPresenter: MainPresenter? = null

    init {
        mainPresenter = MainPresenter(this)
    }

    val navController by lazy(LazyThreadSafetyMode.NONE) {
        Navigation.findNavController(this, R.id.fragment_container)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check user login
        val accessToken = mainPresenter?.getToken()
        println(accessToken)

        if (accessToken != null) {
            //redirect to full app
            DriverObject.driver = mainPresenter?.getDriverData(applicationContext)
            navController.navigate(R.id.main_passanger_fragment)


            if (DriverObject.driver != null) {
                //ride already created
                //replaceFragment(GET_DRIVER_VIEW, null, supportFragmentManager)
                //replaceFragment(PHONE_VIEW, null, supportFragmentManager)

            } else {
                //not created
                //replaceFragment(MAIN_FRAGMENT, null, supportFragmentManager)
                //Coordinator.showDriverView(supportFragmentManager)
                //replaceFragment(PHONE_VIEW, null, supportFragmentManager)
            }

        }


    }


    override fun onPause() {
        super.onPause()
        hideKeyboard(this, findViewById<LinearLayout>(R.id.fragment_container))
    }


    fun showNotification(text: String) {
        mainPresenter?.showNotification(text)
    }


    fun pressBack() {
        super.onBackPressed()
    }


    override fun onBackPressed() {
        mainPresenter?.onBackPressed()
    }
}
