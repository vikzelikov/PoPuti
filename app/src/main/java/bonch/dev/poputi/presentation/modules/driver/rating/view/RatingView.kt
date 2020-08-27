package bonch.dev.poputi.presentation.modules.driver.rating.view

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.di.component.driver.DaggerRatingComponent
import bonch.dev.poputi.di.module.driver.RatingModule
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.modules.driver.rating.RatingComponent
import bonch.dev.poputi.presentation.modules.driver.rating.adapter.RatingAdapter
import bonch.dev.poputi.presentation.modules.driver.rating.presenter.IRatingPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.rating_fragment.*
import javax.inject.Inject

class RatingView : Fragment(), IRatingView {


    @Inject
    lateinit var presenter: IRatingPresenter

    @Inject
    lateinit var ratingAdapter: RatingAdapter

    lateinit var infoBottomSheet: BottomSheetBehavior<*>

    private var skeletonScreen: SkeletonScreen? = null

    private var isReverse = true

    init {
        initDI()

        RatingComponent.ratingComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (RatingComponent.ratingComponent == null) {
            RatingComponent.ratingComponent = DaggerRatingComponent
                .builder()
                .ratingModule(RatingModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rating_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        initAdapter()

        showLoading()

        presenter.getProfile()
    }


    override fun getRating() {
        Handler().postDelayed({
            if (ratingAdapter.list.isEmpty())
                presenter.getRating()
        }, 1000)
    }


    override fun setProfile(profile: Profile) {
        if (profile.firstName != null)
            name?.text = profile.firstName.plus(" ").plus(profile.lastName)

        val img = when {
            profile.photos?.lastOrNull()?.imgUrl != null -> {
                profile.photos?.sortBy { it.id }
                profile.photos?.lastOrNull()?.imgUrl
            }
            profile.imgUser != null -> {
                Uri.parse(profile.imgUser)
            }
            else -> null
        }

        if (img != null)
            Glide.with(img_driver.context).load(img)
                .apply(RequestOptions().centerCrop().circleCrop())
                .error(R.drawable.ic_default_ava)
                .into(img_driver)

        user_rating?.text = if (profile.rating == null) {
            "0.0"
        } else profile.rating.toString()

        car_name?.text = profile.driver?.carName
            ?.plus(" ")
            ?.plus(profile.driver?.carModel)
    }


    override fun setListeners() {
        info.setOnClickListener {
            infoBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }

        sort.setOnClickListener {
            sort()
        }

        sort_icon.setOnClickListener {
            sort()
        }
    }


    private fun sort() {
        sort_icon?.rotation = if (isReverse) {
            isReverse = false
            180f
        } else {
            isReverse = true
            0f
        }

        val newList = arrayListOf<Review>()
        newList.addAll(ratingAdapter.list)
        newList.reverse()
        ratingAdapter.list.clear()
        ratingAdapter.list.addAll(newList)
        ratingAdapter.notifyDataSetChanged()

        comments_recycler?.alpha = 0f
        comments_recycler?.animate()?.alpha(1f)?.duration = 400

    }


    private fun initAdapter() {
        comments_recycler?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = ratingAdapter
        }
    }


    override fun getAdapter() = ratingAdapter


    override fun showEmptyText() {
        text_empty?.visibility = View.VISIBLE
    }


    override fun hideEmptyText() {
        text_empty?.visibility = View.GONE
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {
        skeletonScreen = Skeleton.bind(comments_recycler)
            .adapter(ratingAdapter)
            .load(R.layout.skeleton_rating_item)
            .count(10)
            .show()
    }


    override fun hideLoading() {
        skeletonScreen?.hide()
    }

}