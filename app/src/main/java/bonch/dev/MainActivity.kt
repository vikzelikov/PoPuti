package bonch.dev

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.model.passanger.getdriver.pojo.DriverObject
import bonch.dev.presenter.BasePresenter
import bonch.dev.utils.Constants.GET_DRIVER_VIEW
import bonch.dev.utils.Constants.MAIN_FRAGMENT
import bonch.dev.utils.Constants.PHONE_VIEW
import bonch.dev.utils.Coordinator.replaceFragment
import bonch.dev.utils.Keyboard.hideKeyboard


class MainActivity : AppCompatActivity() {

    private var basePresenter: BasePresenter? = null

    init {
        if (basePresenter == null) {
            basePresenter = BasePresenter(this)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check user login
        val accessToken = basePresenter?.getToken(applicationContext)

        if (accessToken == null) {
            //to signup
            replaceFragment(PHONE_VIEW, null, supportFragmentManager)
            //addFragment(MAIN_FRAGMENT, supportFragmentManager)
        } else {
            //redirect to full app
            DriverObject.driver = basePresenter?.getDriverData(applicationContext)

            if (DriverObject.driver != null) {
                //ride already created
                replaceFragment(GET_DRIVER_VIEW, null, supportFragmentManager)
            } else {
                //not created
                replaceFragment(MAIN_FRAGMENT, null, supportFragmentManager)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        hideKeyboard(this, findViewById<LinearLayout>(R.id.fragment_container))
    }


    fun showNotification(text: String) {
        basePresenter?.showNotification(text)
    }


    fun pressBack(){
        super.onBackPressed()
    }


    override fun onBackPressed() {
        basePresenter?.onBackPressed()
    }
}
