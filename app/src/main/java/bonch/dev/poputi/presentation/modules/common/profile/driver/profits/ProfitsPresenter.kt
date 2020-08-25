package bonch.dev.poputi.presentation.modules.common.profile.driver.profits

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import java.util.*
import javax.inject.Inject

class ProfitsPresenter : BasePresenter<ContractView.IProfitsView>(),
    ContractPresenter.IProfitsPresenter {

    @Inject
    lateinit var profileInteractor: IProfileInteractor


    private val calendar = Calendar.getInstance(Locale("ru"))
    private var timeStamp: Long = -1

    var FEE = 0.05


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    private fun testRandData() {
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

        getView()?.getAdapter()?.list?.clear()
        getView()?.getAdapter()?.list?.addAll(list)
        getView()?.getAdapter()?.notifyDataSetChanged()
    }







    override fun backList() {
        testRandData()

        calcDate(false)
        getView()?.calcTotalProfit()
        getView()?.calcCountOrders()

        getView()?.getAdapter()?.isLeft = true
        getView()?.getAdapter()?.lastPosition = -1
        getView()?.getAdapter()?.notifyDataSetChanged()
    }


    override fun nextList() {
        testRandData()

        calcDate(true)
        getView()?.calcTotalProfit()
        getView()?.calcCountOrders()

        getView()?.getAdapter()?.isLeft = false
        getView()?.getAdapter()?.lastPosition = -1
        getView()?.getAdapter()?.notifyDataSetChanged()
    }


    override fun calcDate(isNext: Boolean?) {
        if (timeStamp == -1L || isNext == null) {
            timeStamp = System.currentTimeMillis()
        }

        when {
            isNext == null -> {
            }

            isNext -> {
                timeStamp += 86400 * 1000
            }

            else -> {
                timeStamp -= 86400 * 1000
            }
        }

        calendar.timeInMillis = timeStamp

//        val date = DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar).toString()

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val mounth = calendar.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG, Locale("ru")
        )

        if (mounth != null) {
            getView()?.setDate("$day".plus(" $mounth"))
        }

        if (day % 4 == 0) {
            getView()?.getAdapter()?.list?.clear()
            getView()?.getAdapter()?.notifyDataSetChanged()

            getView()?.showEmptyText()
        } else getView()?.hideEmptyText()
    }


    override fun instance() = this

}