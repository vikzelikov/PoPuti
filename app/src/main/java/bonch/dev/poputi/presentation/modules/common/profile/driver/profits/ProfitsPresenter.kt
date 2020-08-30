package bonch.dev.poputi.presentation.modules.common.profile.driver.profits

import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProfitsPresenter : BasePresenter<ContractView.IProfitsView>(),
    ContractPresenter.IProfitsPresenter {

    @Inject
    lateinit var profileInteractor: IProfileInteractor


    private var listRides = arrayListOf<RideInfo>()

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private var calendar = Calendar.getInstance(Locale("ru"))
    private var today = calendar.timeInMillis
    private var menuTimestamp: Long = -1

    private var lowDay = -1L

    var FEE = 0.05


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun getRides() {
        calendar = Calendar.getInstance(Locale("ru"))
        today = calendar.timeInMillis
        menuTimestamp = calendar.timeInMillis

        profileInteractor.getStoryRidesDriver { rides, _ ->
            if (rides.isNullOrEmpty()) {
                getView()?.showEmptyText()

                getView()?.hideButton(false)

                getView()?.hideLoading()
            } else {
                val list = arrayListOf<RideInfo>()

                rides.forEach {
                    if (it.driver != null && it.statusId == StatusRide.GET_PLACE.status) {
                        list.add(it)
                    }
                }

                list.sortByDescending {
                    it.rideId
                }

                //save filtered list of rides
                listRides = list

                calcLowDay()

                calcProfits(today, null)

                getView()?.hideLoading()
            }
        }
    }


    override fun backList() {
        calcDate(false)

        checkNeighborDays()

        calcProfits(menuTimestamp, true)
    }


    override fun nextList() {
        calcDate(true)

        checkNeighborDays()

        calcProfits(menuTimestamp, false)
    }


    private fun calcProfits(timeStamp: Long, isLeftAnimation: Boolean?) {
        val list = arrayListOf<RideInfo>()

        val tempCalendar = Calendar.getInstance(Locale("ru"))
        tempCalendar.timeInMillis = timeStamp
        val hours = tempCalendar.get(Calendar.HOUR_OF_DAY)
        val min = tempCalendar.get(Calendar.MINUTE)
        val sec = tempCalendar.get(Calendar.SECOND)

        val startDay = timeStamp - (sec * 1000) - (min * 60 * 1000) - (hours * 60 * 60 * 1000)
        val endDay = startDay + (86400 * 1000)

        getView()?.getAdapter()?.list?.clear()

        listRides.forEach { ride ->
            try {
                val startAt = ride.startAt
                if (startAt != null) {
                    val orderTime = format.parse(startAt)?.time
                    orderTime?.let {
                        if (orderTime in (startDay + 1) until endDay) {
                            list.add(ride)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (list.isEmpty()) {
            getView()?.calcTotalProfit()
            getView()?.showEmptyText()
        } else {
            setProfits(list, isLeftAnimation)
        }
    }


    override fun calcDate(isNext: Boolean?) {
        if (menuTimestamp == -1L || isNext == null) {
            menuTimestamp = System.currentTimeMillis()
        }

        when {
            isNext == null -> {

            }

            isNext -> {
                menuTimestamp += 86400 * 1000
            }

            else -> {
                menuTimestamp -= 86400 * 1000
            }
        }

        calendar.timeInMillis = menuTimestamp

        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val mounth = calendar.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG, Locale("ru")
        )

        if (mounth != null) {
            getView()?.setDate("$day".plus(" $mounth"))
        }
    }


    private fun setProfits(list: ArrayList<RideInfo>, isLeftAnimation: Boolean?) {
        getView()?.getAdapter()?.list?.clear()
        getView()?.getAdapter()?.list?.addAll(list)

        getView()?.calcTotalProfit()
        getView()?.calcCountOrders()

        getView()?.getAdapter()?.isLeft = isLeftAnimation
        getView()?.getAdapter()?.lastPosition = -1

        getView()?.getAdapter()?.notifyDataSetChanged()

        getView()?.hideEmptyText()
    }


    private fun calcLowDay() {
        try {
            val lastRideDate = listRides.last().startAt
            if (lastRideDate != null) {
                val parseDate = format.parse(lastRideDate)

                parseDate?.let {
                    val tempCalendar = Calendar.getInstance(Locale("ru"))
                    tempCalendar.timeInMillis = it.time
                    val hours = tempCalendar.get(Calendar.HOUR_OF_DAY)
                    val min = tempCalendar.get(Calendar.MINUTE)
                    val sec = tempCalendar.get(Calendar.SECOND)

                    lowDay = it.time - (sec * 1000) - (min * 60 * 1000) - (hours * 60 * 60 * 1000)
                }
            }
        } catch (ex: Exception) {
        }
    }


    private fun checkNeighborDays() {
        if (menuTimestamp.minus(86400 * 1000) < lowDay) {
            getView()?.hideButton(true)
        } else {
            getView()?.showButton(true)
        }

        if (menuTimestamp in today - 100000..today + 100000) {
            getView()?.hideButton(false)
        } else {
            getView()?.showButton(false)
        }
    }


    override fun instance() = this

}