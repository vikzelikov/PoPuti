package bonch.dev.poputi.presentation.modules.common.profile.story.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.poputi.presentation.modules.common.profile.story.adapter.StoryAdapter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.story_activity.*
import javax.inject.Inject

class StoryView : AppCompatActivity(), ContractView.IStoryView {

    @Inject
    lateinit var presenter: ContractPresenter.IStoryPresenter


    @Inject
    lateinit var storyAdapter: StoryAdapter


    private var skeletonScreen: SkeletonScreen? = null


    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_activity)

        setListeners()

        initAdapter()

        showLoading()

        val key = "IS_PASSENGER"
        val isForPassenger = intent.getBooleanExtra(key, false)
        presenter.getStory(isForPassenger)
    }


    private fun initAdapter() {
        rides_recycler?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = storyAdapter
        }
    }


    override fun setListeners() {
        back_btn.setOnClickListener {
            finish()
        }
    }


    override fun showDetailStory() {
        val key = "IS_PASSENGER"
        val isForPassenger = intent.getBooleanExtra(key, false)
        val bundle = Bundle()
        bundle.putBoolean(key, isForPassenger)

        val intent = Intent(this, DetailStoryView::class.java)
        startActivity(intent, bundle)
    }


    override fun showEmptyRidesText() {
        text_empty_orders?.visibility = View.VISIBLE
    }


    override fun hideEmptyRidesText() {
        text_empty_orders?.visibility = View.GONE
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun getAdapter() = storyAdapter


    override fun showLoading() {
        skeletonScreen = Skeleton.bind(rides_recycler)
            .adapter(storyAdapter)
            .load(R.layout.skeleton_story_item)
            .count(10)
            .show()
    }


    override fun hideLoading() {
        skeletonScreen?.hide()
    }


    override fun onResume() {
        super.onResume()
        ActiveRide.activeRide = null
    }
}