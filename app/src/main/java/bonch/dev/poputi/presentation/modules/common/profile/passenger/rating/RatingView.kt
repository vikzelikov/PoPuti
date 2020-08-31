package bonch.dev.poputi.presentation.modules.common.profile.passenger.rating

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.profile.Profile
import bonch.dev.poputi.domain.entities.common.rate.Review
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.rating_passenger_activity.*
import javax.inject.Inject

class RatingView : AppCompatActivity(), ContractView.IRatingView {

    @Inject
    lateinit var presenter: ContractPresenter.IRatingPresenter

    @Inject
    lateinit var ratingAdapter: RatingAdapter

    private lateinit var infoBottomSheet: BottomSheetBehavior<*>

    private var skeletonScreen: SkeletonScreen? = null
    private var skeletonName: SkeletonScreen? = null
    private var skeletonRating: SkeletonScreen? = null

    private var isReverse = true

    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.rating_passenger_activity)

        setListeners()

        initAdapter()

        showLoading()

        setBottomSheet()

        presenter.getProfile()

        presenter.getRating()
    }


    override fun setProfile(profile: Profile) {
        if (profile.firstName != null)
            name?.text = profile.firstName.plus(" ").plus(profile.lastName)

        user_rating?.text = if (profile.rating == null) {
            "0.0"
        } else {
            profile.rating.toString()
        }
    }


    private fun initAdapter() {
        comments_recycler?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = ratingAdapter
        }
    }

    override fun getAdapter() = ratingAdapter


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

        back_btn.setOnClickListener {
            finish()
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


    private fun setBottomSheet() {
        infoBottomSheet = BottomSheetBehavior.from<View>(info_bottom_sheet)

        infoBottomSheet.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                on_view_bottom_sheet?.alpha = slideOffset * 0.8f
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    on_view_bottom_sheet?.visibility = View.GONE
                } else {
                    on_view_bottom_sheet?.visibility = View.VISIBLE
                }
            }
        })


        on_view_bottom_sheet.setOnClickListener {
            infoBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


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

        skeletonRating = Skeleton.bind(user_rating)
            .load(R.layout.skeleton_layout)
            .show()

        skeletonName = Skeleton.bind(name)
            .load(R.layout.skeleton_layout)
            .show()
    }


    override fun hideLoading() {
        skeletonScreen?.hide()
        skeletonRating?.hide()
        skeletonName?.hide()

        var layout = user_rating?.layoutParams
        layout?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layout?.width = LinearLayout.LayoutParams.WRAP_CONTENT
        user_rating?.layoutParams = layout

        layout = name?.layoutParams
        layout?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layout?.width = LinearLayout.LayoutParams.MATCH_PARENT
        name?.layoutParams = layout
    }

}
