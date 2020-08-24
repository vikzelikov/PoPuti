package bonch.dev.poputi.presentation.modules.passenger.regular.ride.presenter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.data.repository.common.ride.SearchPlace
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.*
import bonch.dev.poputi.domain.entities.passenger.regular.ride.DateInfo
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.addbanking.view.AddBankCardView
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.view.OfferPriceView
import bonch.dev.poputi.presentation.modules.common.ride.routing.Routing
import bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter.CashEvent
import bonch.dev.poputi.presentation.modules.passenger.regular.RegularDriveComponent
import bonch.dev.poputi.presentation.modules.passenger.regular.ride.view.ContractView
import com.yandex.mapkit.geometry.Point
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class CreateRegularRidePresenter : BasePresenter<ContractView.ICreateRegularDriveView>(),
    ContractPresenter.ICreateRegularDrivePresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    @Inject
    lateinit var routing: Routing

    private var blockRequestHandler: Handler? = null

    private var cashEvent: CashEvent

    val OFFER_PRICE = 1
    val ADD_BANK_CARD = 2
    private val AVERAGE_PRICE = "AVERAGE_PRICE"
    private val NOT_DESCRIPTION = "Место"
    private val BLOCK_REQUEST_GEOCODER = 1500L


    private var fromPoint: Point? = null
    private var toPoint: Point? = null

    var isFromMapSearch = true
    var isBlockGeocoder = false
    var isOnRouting = false
    private var isBlockRequest = false
    private var onResponseScheduler = false
    private var onResponseChangeRide = false
    private var checkOnServerResponse: AtomicInteger = AtomicInteger(0)


    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)

        cashEvent = CashEvent(getDriverInteractor)
    }


    override fun checkOnEditRide() {
        Routing.mapObjectsDriver = null
        Routing.mapObjects = null

        val ride = ActiveRide.activeRide
        if (ride != null) {
            val from = ride.position
            val to = ride.destination
            val fromLat = ride.fromLat
            val fromLng = ride.fromLng
            val toLat = ride.toLat
            val toLng = ride.toLng
            val price = ride.price
            val days = getDays(ride.dateInfo)
            val time = ride.dateInfo?.time
            val comment = ride.comment

            if (from != null && fromLat != null && fromLng != null) {
                val address = Address()
                address.address = from
                val point = AddressPoint(fromLat, fromLng)
                address.point = point

                Coordinate.fromAdr = address
            }

            if (to != null && toLat != null && toLng != null) {
                val address = Address()
                address.address = to
                val point = AddressPoint(toLat, toLng)
                address.point = point

                Coordinate.toAdr = address
            }

            val fromAdr = Coordinate.fromAdr
            val toAdr = Coordinate.toAdr
            if (fromAdr != null && toAdr != null) checkAddressPoints(fromAdr, toAdr)

            if (from != null && to != null) {
                getView()?.setAddressView(true, from)
                getView()?.setAddressView(false, to)
            }
            //todo average price and selected bank card
            if (price != null) getView()?.offerPriceDone(price, 555)
            if (days != null) getView()?.setDays(days)
            parseTime(time)?.let { getView()?.setTime(it) }
            if (comment != null) getView()?.setComment(comment)
            setSelectedBankCard(
                BankCard(0,"google", null, null, R.drawable.ic_google_pay, true)
            )

            getView()?.hideMapMarker()
            getView()?.showRouteBtn()
            isBlockGeocoder = true

        } else {
            isBlockGeocoder = false
            isOnRouting = false
        }
    }


    override fun createDone(): Boolean {
        var isSuccess = false
        val from = Coordinate.fromAdr
        val to = Coordinate.toAdr

        if (from != null && to != null) {
            //remove all old routes
            removeRoute()

            //cash data (both of adr)
            cashEvent.saveCashSuggest(from)
            cashEvent.saveCashSuggest(to)

            getView()?.showStartUI(false)
            getView()?.hideMapMarker()
            getView()?.hideAllBottomSheet()
            getView()?.hideMainInfoLayout()
            getView()?.showRouteBtn()

            checkAddressPoints(from, to)

            Handler().postDelayed({
                getView()?.showMainInfoLayout()
            }, 2000)

            isSuccess = true
            isBlockGeocoder = true
        }

        return isSuccess
    }


    override fun requestGeocoder(point: Point?) {
        if (!isBlockGeocoder && !isBlockRequest && point != null) {
            //send request and set block for several seconds
            getDriverInteractor.requestGeocoder(point) { address, responsePoint ->
                responseGeocoder(address, responsePoint)
            }
            isBlockRequest = true
        }
    }


    private fun responseGeocoder(address: String?, point: Point) {
        val actualFocus = getView()?.getActualFocus()

        if (address != null) {
            if (isFromMapSearch || (actualFocus != null && !actualFocus)) {
                Coordinate.fromAdr = Address(
                    0,
                    address,
                    NOT_DESCRIPTION,
                    null,
                    AddressPoint(
                        point.latitude,
                        point.longitude
                    )
                )
            } else {
                Coordinate.toAdr = Address(
                    0,
                    address,
                    NOT_DESCRIPTION,
                    null,
                    AddressPoint(
                        point.latitude,
                        point.longitude
                    )
                )
            }

            if (actualFocus != null && !actualFocus) {
                getView()?.setAddressView(true, address)
            } else {
                getView()?.setAddressView(isFromMapSearch, address)
            }
        }
    }


    override fun requestSuggest(query: String) {
        if (query.length > 2) {
            getDriverInteractor.initRealm()
            //first check cash, then go to net
            //try to get data from cash
            if (!isBlockRequest) {
                val cashRequest = getDriverInteractor.getCashRequest(query)

                if (!cashRequest.isNullOrEmpty()) {
                    //set in view
                    val adapter = getView()?.getAddressesAdapter()
                    adapter?.list?.clear()
                    adapter?.list?.addAll(cashRequest)
                    adapter?.notifyDataSetChanged()
                    isBlockRequest = true

                } else {
                    getDriverInteractor.requestSuggest(query, getUserPoint()) {
                        responseSuggest(it)
                    }
                }

                isBlockRequest = true
            }

        } else {
            clearSuggest()
        }

        if (query.isEmpty()) {
            getCashSuggest()
        }
    }


    private fun responseSuggest(suggestResult: ArrayList<Address>) {
        //cash request
        getDriverInteractor.saveCashRequest(suggestResult)

        //set in view
        val adapter = getView()?.getAddressesAdapter()
        adapter?.list = suggestResult
        adapter?.notifyDataSetChanged()
    }


    override fun checkAddressPoints(fromAddress: Address, toAddress: Address) {
        val fromUri = fromAddress.uri
        val toUri = toAddress.uri
        val fromP = fromAddress.point
        val toP = toAddress.point

        if (fromP != null) {
            fromPoint = Point(fromP.latitude, fromP.longitude)
        }

        if (toP != null) {
            toPoint = Point(toP.latitude, toP.longitude)
        }

        if (fromPoint != null && toPoint != null) {
            submitRoute()
        } else {
            //50 - ok length for detect correct URI or no
            if (fromUri != null) {
                if (fromUri.length > 50) {
                    fromPoint = getPoint(fromUri)

                    submitRoute()
                } else {
                    SearchPlace().request(fromUri) { point, _ ->
                        fromPoint = point

                        submitRoute()
                    }
                }
            }

            if (toUri != null) {
                if (toUri.length > 50) {
                    toPoint = getPoint(toUri)

                    submitRoute()
                } else {
                    SearchPlace().request(toUri) { point, _ ->
                        toPoint = point

                        submitRoute()
                    }
                }
            }
        }
    }


    override fun submitRoute() {
        val from = fromPoint
        val to = toPoint
        val map = getView()?.getMap()

        if (from != null && to != null && map != null) {
            //update points
            Coordinate.fromAdr?.point =
                AddressPoint(
                    from.latitude,
                    from.longitude
                )
            Coordinate.toAdr?.point =
                AddressPoint(
                    to.latitude,
                    to.longitude
                )

            routing.submitRequest(from, to, true, map)

            isOnRouting = true
        }
    }


    override fun onClickItem(address: Address) {
        if (isFromMapSearch) {
            fromPoint = null
        } else toPoint = null

        getView()?.onClickItem(address, isFromMapSearch)

        createDone()
    }


    override fun touchCrossAddress(isFrom: Boolean) {
        if (!isFrom) {
            Coordinate.toAdr = null
        }

        getView()?.removeAddressesView(isFrom)
    }


    override fun touchAddress(isFrom: Boolean, isShowKeyboard: Boolean) {

        getView()?.expandedBottomSheet(isFrom, isShowKeyboard)

        //update current focus
        isFromMapSearch = isFrom
    }


    override fun touchMapBtn(isFrom: Boolean) {
        isFromMapSearch = isFrom

        getView()?.addressesMapViewChanged(isFrom)

        removeRoute()
        isBlockGeocoder = false
    }


    override fun touchAddressMapMarkerBtn() {
        val res = createDone()

        if (!res) {
            getView()?.showStartUI(true)
        }
    }


    override fun clearSuggest() {
        val adapter = getView()?.getAddressesAdapter()
        adapter?.list?.clear()
        adapter?.notifyDataSetChanged()
    }


    override fun getCashSuggest() {
        val suggest = cashEvent.getCashSuggest()

        if (suggest != null) {
            val adapter = getView()?.getAddressesAdapter()
            adapter?.list?.clear()
            adapter?.list?.addAll(suggest)
            adapter?.notifyDataSetChanged()
        }
    }


    override fun showRoute() {
        routing.showRoute()
    }


    override fun showMyPosition() {
        val userPoint = getUserPoint()

        if (userPoint != null) {
            getView()?.moveCamera(userPoint)
        }
    }


    private fun getUserPoint(): Point? {
        val userLocation = getView()?.getUserLocationLayer()
        return userLocation?.cameraPosition()?.target
    }


    override fun removeRoute() {
        routing.removeRoute()
    }


    //parse and get URI
    private fun getPoint(uri: String): Point? {
        var coordinate: Point?
        val lat: Double
        val long: Double
        var str: List<String>

        try {
            str = uri.split("=")
            str = str[1].split("%")
            long = str[0].toDouble()
            lat = str[1].substring(2, str[1].length - 4).toDouble()

            coordinate = Point(lat, long)
        } catch (ex: IndexOutOfBoundsException) {
            coordinate = null
        }

        return coordinate
    }


    private fun getRide(): RideInfo? {
        var resultRide: RideInfo? = null

        val view = getView()
        if (view != null && view.isDataComplete()) {
            val rideInfo = getView()?.getRideInfo()

            getView()?.showLoading()

            rideInfo?.fromAdr = Coordinate.fromAdr
            rideInfo?.toAdr = Coordinate.toAdr

            if (rideInfo != null) {
                val ride = RideInfo()
                ride.rideId = ActiveRide.activeRide?.rideId
                ride.position = rideInfo.fromAdr?.address
                ride.fromLat = rideInfo.fromAdr?.point?.latitude
                ride.fromLng = rideInfo.fromAdr?.point?.longitude
                ride.destination = rideInfo.toAdr?.address
                ride.toLat = rideInfo.toAdr?.point?.latitude
                ride.toLng = rideInfo.toAdr?.point?.longitude
                ride.price = rideInfo.price
                ride.comment = rideInfo.comment
                ride.statusId = StatusRide.REGULAR_RIDE.status

                val dateInfo = getDateInfo()
                ride.dateInfo = dateInfo

                //save ride
                ActiveRide.activeRide = ride

                resultRide = ride
            }
        }

        return resultRide
    }


    //create ride with SERVER
    override fun createRide() {
        val res = App.appComponent.getContext().resources

        val ride = getRide()

        if (ride != null) {
            //create ride with SERVER
            getDriverInteractor.createRide(ride) { isSuccess ->
                if (isSuccess) {
                    //next step
                    val dateInfo = ride.dateInfo
                    if (dateInfo != null) {
                        getDriverInteractor.createRideSchedule(dateInfo) { result ->
                            if (result) {
                                getView()?.finishActivity()
                            } else {
                                ActiveRide.activeRide = null
                                errorResponse()
                            }
                        }
                    } else {
                        getView()?.showNotification(res.getString(R.string.tryAgain))
                        getView()?.hideLoading()
                    }
                } else {
                    getView()?.hideLoading()

                    getView()?.showNotification(res.getString(R.string.tryAgain))
                }
            }
        } else getView()?.showNotification(res.getString(R.string.someError))
    }


    override fun updateRide() {
        val ride = getRide()
        val dateInfo = ride?.dateInfo

        if (ride != null && dateInfo != null) {
            //update ride with SERVER
            getDriverInteractor.updateRide(ride) { result ->
                onResponseChangeRide = result
                onResponseUpdate()
            }

            //update schedule with SERVER
            getDriverInteractor.updateRideSchedule(dateInfo) { result ->
                onResponseScheduler = result
                onResponseUpdate()
            }
        } else {
            errorResponse()
        }
    }


    private fun onResponseUpdate() {
        val check = checkOnServerResponse.incrementAndGet()
        if (check == 2) {
            if (onResponseChangeRide && onResponseScheduler) {
                getView()?.finishActivity()
            } else {
                errorResponse()
            }

            getView()?.hideLoading()

            //reset for next trying
            checkOnServerResponse = AtomicInteger(0)
        }
    }


    private fun errorResponse() {
        val res = App.appComponent.getContext().resources
        getView()?.showNotification(res.getString(R.string.tryAgain))

        checkOnServerResponse = AtomicInteger(0)

        getView()?.hideLoading()
    }


    private fun getDateInfo(): DateInfo? {
        val dateInfo = DateInfo()
        val date = getTime()
        val days = getView()?.getDays()
        days?.forEachIndexed { i, day ->
            when (i) {
                0 -> dateInfo.monday = day
                1 -> dateInfo.tuesday = day
                2 -> dateInfo.wednesday = day
                3 -> dateInfo.thursday = day
                4 -> dateInfo.friday = day
                5 -> dateInfo.saturday = day
                6 -> dateInfo.sunday = day
            }
        }

        dateInfo.time = date
        return dateInfo
    }


    private fun getTime(): String? {
        val milliseconds = getView()?.getTime()
        var date: Date? = null
        if (milliseconds != null) {
            val minutes: Int = (((milliseconds / (1000 * 60)) % 60).toInt())
            val hours: Int = (((milliseconds / (1000 * 60 * 60)) % 24).toInt())

            val cal = Calendar.getInstance()
            cal[Calendar.HOUR_OF_DAY] = hours
            cal[Calendar.MINUTE] = minutes
            date = cal.time
        }

        return DateFormat.format("yyyy-MM-dd HH:mm:ss", date).toString()
    }


    override fun offerPriceDone(data: Intent?) {
        val price = data?.getIntExtra(OFFER_PRICE.toString(), 0)
        val averagePrice = data?.getIntExtra(AVERAGE_PRICE, 0)

        if (price != null && averagePrice != null) {
            getView()?.offerPriceDone(price, averagePrice)
        }
    }


    override fun addBankCardDone(data: Intent?) {
        val bankCard = data?.getParcelableExtra<BankCard>(ADD_BANK_CARD.toString())

        val paymentCard = BankCard(
            0,
            bankCard?.numberCard,
            bankCard?.validUntil,
            bankCard?.cvc,
            bankCard?.img
        )

        val adapter = getView()?.getPaymentsAdapter()
        adapter?.list?.let {
            if (it.isNotEmpty()) {
                paymentCard.id = it.last().id + 1
            }
        }
        adapter?.list?.add(paymentCard)
        adapter?.notifyDataSetChanged()

        getDriverInteractor.saveBankCard(paymentCard)
    }


    override fun getBankCards(): ArrayList<BankCard> {
        return getDriverInteractor.getBankCards()
    }


    override fun startProcessBlockRequest() {
        if (blockRequestHandler == null) {
            blockRequestHandler = Handler()
        }

        blockRequestHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlockRequest = false
                blockRequestHandler?.postDelayed(this, BLOCK_REQUEST_GEOCODER)
            }
        }, 0)

        requestGeocoder(getUserPoint())
    }


    override fun onObjectUpdate() {
        if (blockRequestHandler == null) {
            startProcessBlockRequest()
        }
    }


    private fun stopProcessBlockRequest() {
        blockRequestHandler?.removeCallbacksAndMessages(null)
        blockRequestHandler = null
    }


    override fun offerPrice(context: Context, fragment: Fragment) {
        val intent = Intent(context, OfferPriceView::class.java)
        fragment.startActivityForResult(intent, OFFER_PRICE)
    }


    override fun addBankCard(context: Context, fragment: Fragment) {
        val intent = Intent(context, AddBankCardView::class.java)
        fragment.startActivityForResult(intent, ADD_BANK_CARD)
    }


    private fun getDays(dateInfo: DateInfo?): BooleanArray? {
        var days = BooleanArray(7)

        if (dateInfo != null) {
            days = booleanArrayOf(
                dateInfo.monday,
                dateInfo.tuesday,
                dateInfo.wednesday,
                dateInfo.thursday,
                dateInfo.friday,
                dateInfo.saturday,
                dateInfo.sunday
            )
        }

        return days
    }


    private fun parseTime(time: String?): String? {
        var resultTime: String? = null
        if (time != null) {
            if (time.length > 8) {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                try {
                    val date = format.parse(time)
                    if (date != null) {
                        val calendar = GregorianCalendar.getInstance()
                        calendar.time = date

                        resultTime = calendar[Calendar.HOUR_OF_DAY].toString()
                            .plus(":")
                            .plus(calendar[Calendar.MINUTE])

                    }
                } catch (e: Exception) {
                }
            } else {
                try {
                    val splitTime = time.split(":")
                    resultTime = splitTime[0].plus(":").plus(splitTime[1])
                } catch (ex: IndexOutOfBoundsException) {
                }
            }
        }

        return resultTime
    }


    override fun removeTickSelected() {
        getView()?.removeTickSelected()
    }


    override fun setSelectedBankCard(bankCard: BankCard) {
        getView()?.setSelectedBankCard(bankCard)
    }


    override fun instance() = this


    override fun onDestroy() {
        stopProcessBlockRequest()
        cashEvent.cashSuggests = null
        getDriverInteractor.closeRealm()
    }

}