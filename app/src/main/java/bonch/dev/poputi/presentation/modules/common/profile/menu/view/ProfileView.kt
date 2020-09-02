package bonch.dev.poputi.presentation.modules.common.profile.menu.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.poputi.App
import bonch.dev.poputi.MainActivity
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.common.DaggerProfileComponent
import bonch.dev.poputi.di.module.common.ProfileModule
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.domain.utils.Keyboard
import bonch.dev.poputi.presentation.interfaces.ParentEmptyHandler
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.modules.common.profile.menu.presenter.IProfilePresenter
import bonch.dev.poputi.route.MainRouter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
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

    var changeGeoMap: ParentHandler<Address>? = null
    var stopSearch: ParentEmptyHandler? = null

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

        profilePresenter.getMyCity()?.address?.let {
            setMyCity(it)
        }

        if (isForPassenger)
            setViewForPassenger()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && requestCode == profilePresenter.instance().PROFILE_DETAIL_VIEW && resultCode == Activity.RESULT_OK) {
            profilePresenter.profileDataResult(data)

            CacheProfile.profile?.let { setProfile(it) }
        }

        if (resultCode == EXIT) {
            profilePresenter.logout()
        }

        if (resultCode == CHECKOUT) {
            //redirect ot driver interface
            MainRouter.showView(R.id.show_main_driver_fragment, getNavHost(), null)
        }

        if (requestCode == profilePresenter.instance().CONFIRM_PERSON && resultCode == Activity.RESULT_OK) {
            showModerateIcon()
        }

        if (requestCode == Geo.SELECT_CITY && resultCode == Activity.RESULT_OK) {
            Geo.selectedCity?.let {
                changeGeoMap?.let { geo -> geo(it) }

                it.address?.let { city -> setMyCity(city) }
            }
        }

        if (requestCode == profilePresenter.instance().CHANGE_LANG && resultCode == Activity.RESULT_OK) {
            data?.getBooleanExtra(profilePresenter.instance().LANG, false)
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
            profilePresenter.confirmPerson(this)
        }

        add_card.setOnClickListener {
            profilePresenter.addBankCard()
        }

        story_orders.setOnClickListener {
            profilePresenter.showStoryOrders(isForPassenger)
        }

        profits.setOnClickListener {
            profilePresenter.showProfits()
        }

        change_lang.setOnClickListener {
            profilePresenter.changeLang(this)
        }

        support.setOnClickListener {
            profilePresenter.showSupport()
        }

        condition_use.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.eula)))
            startActivity(browserIntent)
        }

        rating.setOnClickListener {
            profilePresenter.showRating()
        }

        about_car.setOnClickListener {
            profilePresenter.showCarInfo()
        }

        select_city.setOnClickListener {
            profilePresenter.showSelectCity(this)
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

        val listPhotos = arrayListOf<Photo>()
        profileData.photos?.forEach {
            if (it.imgName == "photo") listPhotos.add(it)
        }
        var img = when {
            listPhotos.lastOrNull()?.imgUrl != null -> {
                listPhotos.sortBy { it.id }
                listPhotos.lastOrNull()?.imgUrl
            }

            else -> null
        }

        if (profileData.imgUser != null)
            img = profileData.imgUser

        if (img != null && img_user != null)
            Glide.with(img_user.context).load(img)
                .apply(RequestOptions().centerCrop().circleCrop())
                .error(R.drawable.ic_default_ava)
                .into(img_user)

        user_rating?.text = if (profileData.rating == null) {
            "0.0"
        } else profileData.rating.toString()


        profileData.photos?.let {
            profilePresenter.checkModerateVerification(it)
        }

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


    override fun setMyCity(address: String) {
        subtitle_city?.text = address
        subtitle_city?.visibility = View.VISIBLE
    }


    override fun showModerateIcon() {
        moderate_icon?.visibility = View.VISIBLE
    }


    override fun stopSearchOrders() {
        stopSearch?.let { it() }
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