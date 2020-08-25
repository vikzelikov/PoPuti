package bonch.dev.poputi.presentation.modules.common.profile.driver.profits

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.poputi.R
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import com.ethanhua.skeleton.Skeleton
import kotlinx.android.synthetic.main.profits_activity.*
import javax.inject.Inject


class ProfitsView : AppCompatActivity(), ContractView.IProfitsView {

    @Inject
    lateinit var presenter: ContractPresenter.IProfitsPresenter


    @Inject
    lateinit var profitsAdapter: ProfitsAdapter


    init {
        ProfileComponent.profileComponent?.inject(this)

        presenter.instance().attachView(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profits_activity)

        setListeners()

        initAdapter()

        presenter.calcDate(null)
        calcTotalProfit()
        calcCountOrders()

//        val skeletonScreen = Skeleton.bind(orders_recycler)
//            .adapter(profitsAdapter)
//            .load(R.layout.skeleton_layout)
//            .show()
    }


    override fun setListeners() {
        btn_left.setOnClickListener {
            presenter.backList()

            animateButton(btn_left)
        }

        btn_right.setOnClickListener {
            presenter.nextList()

            animateButton(btn_right)
        }

        back_btn.setOnClickListener {
            finish()
        }
    }


    private fun initAdapter() {
        orders_recycler?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = profitsAdapter
        }
    }


    override fun setDate(time: String) {
        day?.text = time
    }


    override fun calcTotalProfit() {
        var totalProfit = 0

        profitsAdapter.list.forEach {
            it.price?.let { price ->
                totalProfit += price
            }
        }

        val resultProfit = totalProfit.times(1 - presenter.instance().FEE).toInt()
        total_profit?.text = resultProfit.toString().plus(" ₽")
    }


    override fun calcCountOrders() {
        val count = profitsAdapter.list.size
        if (count > 0) {
            var text: String?

            text = when (count % 10) {
                1 -> {
                    getString(R.string.order)
                }

                2 -> {
                    getString(R.string.orderA)
                }

                3 -> {
                    getString(R.string.orderA)
                }

                4 -> {
                    getString(R.string.orderA)
                }

                else -> {
                    getString(R.string.orderOV)
                }
            }

            if (count in 11..19) {
                text = getString(R.string.orderOV)
            }

            count_orders?.text = "$count".plus(" $text")
        } else showEmptyText()
    }


    override fun showEmptyText() {
        count_orders?.text = "0 ".plus(getString(R.string.orderOV))
        orders_recycler?.visibility = View.GONE
        text_empty_orders?.visibility = View.VISIBLE
    }


    override fun hideEmptyText() {
        orders_recycler?.visibility = View.VISIBLE
        text_empty_orders?.visibility = View.GONE
    }


    private fun animateButton(btn: ImageButton) {
        val a = AnimationUtils.loadAnimation(this, R.anim.btn_scale)
        a.reset()
        btn.clearAnimation()
        btn.startAnimation(a)
    }


    override fun getAdapter() = profitsAdapter


    override fun getNavHost(): NavController? = null


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {}


    override fun showLoading() {

    }


    override fun hideLoading() {

    }

}