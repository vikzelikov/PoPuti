package bonch.dev.poputi.presentation.modules.common.profile.passenger.story.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.poputi.presentation.modules.common.profile.passenger.story.adapter.StoryAdapter
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import kotlinx.android.synthetic.main.story_activity.*
import java.util.*
import javax.inject.Inject

class StoryView : AppCompatActivity(), ContractView.IStoryView {

    @Inject
    lateinit var presenter: ContractPresenter.IStoryPresenter


    @Inject
    lateinit var storyAdapter: StoryAdapter


    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_activity)

        presenter.getStory()

        setListeners()

        initAdapter()

    }


    private fun initAdapter() {
        rides_recycler?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = storyAdapter
        }

        val min = 0
        val max = 23

        val r = Random()
        val i1 = r.nextInt(max - min + 1) + min


        val list = arrayListOf<RideInfo>()
        for (i in 4..i1) {
            val ride = RideInfo()

            ride.position = "Улица Тверская, дом 121, подъезд 12, Санкт-Петербург"
            ride.destination = "Улица Вокзальная, дом 13/А"
            val rand = Random()
            val abcd = rand.nextInt(100)

            ride.price = i * abcd

            list.add(ride)
        }

        storyAdapter.list?.clear()
        storyAdapter?.list?.addAll(list)
        storyAdapter?.notifyDataSetChanged()
    }


    override fun setListeners() {
        back_btn.setOnClickListener {
            finish()
        }
    }


    override fun showDetailStory() {
        val intent = Intent(this, DetailStoryView::class.java)
        startActivity(intent)
    }


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun getAdapter() = storyAdapter


    override fun showLoading() {

    }

    override fun hideLoading() {

    }


}