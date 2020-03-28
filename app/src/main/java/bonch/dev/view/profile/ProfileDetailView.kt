package bonch.dev.view.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bonch.dev.R
import bonch.dev.presenter.profile.ProfileDetailPresenter
import bonch.dev.utils.Keyboard
import kotlinx.android.synthetic.main.profile_detail_activity.view.*


class ProfileDetailView : AppCompatActivity() {

    private var profileDetailPresenter: ProfileDetailPresenter? = null

    init {
        if (profileDetailPresenter == null) {
            profileDetailPresenter = ProfileDetailPresenter(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_detail_activity)

        val root = findViewById<View>(R.id.rootLinearLayout)

        setListeners(root)
    }


    private fun setListeners(root: View) {
        val logout = root.logout
        val backBtn = root.back_btn

        backBtn.setOnClickListener {
            //save data
            Keyboard.hideKeyboard(this, root)
            finish()
        }


        logout.setOnClickListener {
            profileDetailPresenter?.logout()
        }
    }
}
