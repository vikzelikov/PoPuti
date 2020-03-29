package bonch.dev.view.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import bonch.dev.R
import bonch.dev.presenter.profile.ProfilePresenter
import bonch.dev.utils.Constants
import kotlinx.android.synthetic.main.profile_fragment.view.*

class ProfileView : Fragment() {

    private var profilePresenter: ProfilePresenter? = null

    init {
        if(profilePresenter == null){
            profilePresenter = ProfilePresenter(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.profile_fragment, container, false)

        profilePresenter?.root = root

        profilePresenter?.getProfileDataDB()

        setListeners(root)

        return root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && requestCode == Constants.PROFILE_FULL && resultCode == Activity.RESULT_OK) {
            profilePresenter?.profileDataResult(data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun setListeners(root: View) {
        val miniProfile = root.mini_profile
        val confirmPerson = root.confirm_person
        val addCard = root.add_card
        val storyOrders = root.story_orders
        val notifications = root.notifications
        val setRatingApp = root.set_rating_app
        val changeLang = root.change_lang
        val support = root.support
        val conditionUse = root.condition_use
        val checkoutDriver = root.checkout_driver

        miniProfile.setOnClickListener{
            profilePresenter?.getFullProfile()
        }

        confirmPerson.setOnClickListener {
            Toast.makeText(context, "Confirm person", Toast.LENGTH_SHORT).show()
        }

        addCard.setOnClickListener {
            Toast.makeText(context, "Add card", Toast.LENGTH_SHORT).show()
        }

        storyOrders.setOnClickListener {
            Toast.makeText(context, "Story orders", Toast.LENGTH_SHORT).show()
        }

        notifications.setOnClickListener {
            Toast.makeText(context, "Notifications", Toast.LENGTH_SHORT).show()
        }

        setRatingApp.setOnClickListener {
            Toast.makeText(context, "Rating app", Toast.LENGTH_SHORT).show()
        }

        changeLang.setOnClickListener {
            Toast.makeText(context, "Change lang", Toast.LENGTH_SHORT).show()
        }

        support.setOnClickListener {
            Toast.makeText(context, "Support", Toast.LENGTH_SHORT).show()
        }

        conditionUse.setOnClickListener {
            Toast.makeText(context, "Conditions user", Toast.LENGTH_SHORT).show()
        }

        checkoutDriver.setOnClickListener {
            Toast.makeText(context, "To driver", Toast.LENGTH_SHORT).show()
            profilePresenter?.checkoutToDriver()
        }
    }
}