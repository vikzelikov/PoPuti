package bonch.dev

import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.data.repository.passanger.getdriver.pojo.DriverObject
import bonch.dev.presentation.base.MainPresenter
import bonch.dev.domain.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.domain.utils.Constants.PHONE_VIEW
import bonch.dev.route.Coordinator
import bonch.dev.route.Coordinator.replaceFragment
import bonch.dev.domain.utils.Keyboard.hideKeyboard
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private var mainPresenter: MainPresenter? = null

    init {
        mainPresenter = MainPresenter(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check user login
        val accessToken = mainPresenter?.getToken(applicationContext)

        if (accessToken == null) {
            //to signup
            replaceFragment(PHONE_VIEW, null, supportFragmentManager)
            //addFragment(MAIN_FRAGMENT, supportFragmentManager)
            //Coordinator.showDriverView(supportFragmentManager)

        } else {
            //redirect to full app
            DriverObject.driver = mainPresenter?.getDriverData(applicationContext)

            if (DriverObject.driver != null) {
                //ride already created
                //replaceFragment(GET_DRIVER_VIEW, null, supportFragmentManager)
                replaceFragment(PHONE_VIEW, null, supportFragmentManager)

            } else {
                //not created
                //replaceFragment(MAIN_FRAGMENT, null, supportFragmentManager)
                //Coordinator.showDriverView(supportFragmentManager)
                replaceFragment(PHONE_VIEW, null, supportFragmentManager)
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
