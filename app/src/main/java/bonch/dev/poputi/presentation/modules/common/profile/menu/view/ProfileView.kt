package bonch.dev.poputi.presentation.modules.common.profile.menu.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.common.DaggerProfileComponent
import bonch.dev.poputi.di.module.common.ProfileModule
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.domain.utils.Keyboard
import bonch.dev.poputi.presentation.modules.common.profile.menu.presenter.IProfilePresenter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import bonch.dev.poputi.route.MainRouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.profile_fragment.*
import javax.inject.Inject


/**
 * Common class for profile
 * (default UI set for driver, call setViewForPassenger() for checkout view)
 * */


class ProfileView : Fragment(), IProfileView {

    @Inject
    lateinit var profilePresenter: IProfilePresenter

    private val PROFILE_DETAIL_VIEW = 11
    private val EXIT = -2
    private val CHECKOUT = -3

    var isForPassenger = true


    init {
        initDI()

        ProfileComponent.profileComponent?.inject(this)

        profilePresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (ProfileComponent.profileComponent == null) {
            ProfileComponent.profileComponent = DaggerProfileComponent
                .builder()
                .profileModule(ProfileModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilePresenter.getProfile()

        setListeners()

        if (isForPassenger)
            setViewForPassenger()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && requestCode == PROFILE_DETAIL_VIEW && resultCode == Activity.RESULT_OK) {
            profilePresenter.profileDataResult(data)
        }

        if (resultCode == EXIT) {
            profilePresenter.logout()
        }

        if (resultCode == CHECKOUT) {
            //redirect ot driver interface
            MainRouter.showView(R.id.show_main_driver_fragment, getNavHost(), null)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun setViewForPassenger() {
        about_car?.visibility = View.GONE
        profits?.visibility = View.GONE

        rating?.visibility = View.VISIBLE
        confirm_person?.visibility = View.VISIBLE

        checkout_account?.text = resources.getString(R.string.becomeDriver)
    }


    override fun setListeners() {
        mini_profile.setOnClickListener {
            profilePresenter.showFullProfile(this)
        }

        confirm_person.setOnClickListener {
            Toast.makeText(context, "Confirm person", Toast.LENGTH_SHORT).show()
        }

        add_card.setOnClickListener {
            profilePresenter.addBankCard()
        }

        story_orders.setOnClickListener {
            Toast.makeText(context, "Story orders", Toast.LENGTH_SHORT).show()
        }

        profits.setOnClickListener {
            Toast.makeText(context, "Profits", Toast.LENGTH_SHORT).show()
        }

        change_lang.setOnClickListener {
            Toast.makeText(context, "Change lang", Toast.LENGTH_SHORT).show()
        }

        support.setOnClickListener {
            Toast.makeText(context, "Support", Toast.LENGTH_SHORT).show()
        }

        condition_use.setOnClickListener {
            Toast.makeText(context, "Conditions user", Toast.LENGTH_SHORT).show()
        }

        rating.setOnClickListener {
            Toast.makeText(context, "Rating", Toast.LENGTH_SHORT).show()
        }

        about_car.setOnClickListener {
            Toast.makeText(context, "About car", Toast.LENGTH_SHORT).show()
        }

        checkout_account.setOnClickListener {
            profilePresenter.checkoutAccount(isForPassenger, this)
        }
    }


    override fun showNotification(text: String) {
        val activity = activity as? MainActivity
        activity?.let {
            activity.showNotification(text)
        }
    }


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


    override fun setProfile(profileData: Profile) {
        if (profileData.firstName != null)
            name_user?.text = profileData.firstName.plus(" ").plus(profileData.lastName)

        val img = when {
            profileData.photos?.lastOrNull()?.imgUrl != null -> {
                profileData.photos?.sortBy { it.id }
                profileData.photos?.lastOrNull()?.imgUrl
            }
            profileData.imgUser != null -> {
                Uri.parse(profileData.imgUser)
            }
            else -> null
        }

        if (img != null)
            Glide.with(img_user.context).load(img)
                .apply(RequestOptions().centerCrop().circleCrop())
                .error(R.drawable.ic_default_ava)
                .into(img_user)

        user_rating?.text = if (profileData.rating == null) {
            "0.0"
        } else profileData.rating.toString()


    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.getNavHost()
    }


    override fun hideKeyboard() {
        val activity = activity as? MainActivity
        activity?.let {
            Keyboard.hideKeyboard(it, profile_container)
        }
    }


    override fun isPassanger(): Boolean {
        return isForPassenger
    }


    override fun onDestroy() {
        profilePresenter.instance().detachView()
        profilePresenter.onDestroy()
        super.onDestroy()
    }
}