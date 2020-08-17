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
    private var isBlockRequest = false
    private var checkCreateResponse: AtomicInteger = AtomicInteger(0)
    private var onResponseScheduler = false
    private var onResponseChangeStatus = false


    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)

        cashEvent = CashEvent(getDriverInteractor)
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

        //50 - ok length for detect correct URI or no
        if (fromUri != null) {
            if (fromUri.length > 50) {
                fromPoint = getPoint(fromUri)
            } else {
                SearchPlace().request(fromUri) { point, _ ->
                    fromPoint = point
                }
            }
        }

        if (toUri != null) {
            if (toUri.length > 50) {
                toPoint = getPoint(toUri)
            } else {
                SearchPlace().request(toUri) { point, _ ->
                    toPoint = point

                    submitRoute()
                }
            }
        }

        submitRoute()
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
        }
    }


    override fun onClickItem(address: Address) {
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


    //create ride with SERVER
    override fun createRide() {
        val view = getView()
        val res = App.appComponent.getContext().resources

        if (view != null && view.isDataComplete()) {
            val rideInfo = getView()?.getRideInfo()

            getView()?.showLoading()

            rideInfo?.fromAdr = Coordinate.fromAdr
            rideInfo?.toAdr = Coordinate.toAdr

            if (rideInfo != null) {
                val ride = RideInfo()
                ride.position = rideInfo.fromAdr?.address
                ride.fromLat = rideInfo.fromAdr?.point?.latitude
                ride.fromLng = rideInfo.fromAdr?.point?.longitude
                ride.destination = rideInfo.toAdr?.address
                ride.toLat = rideInfo.toAdr?.point?.latitude
                ride.toLng = rideInfo.toAdr?.point?.longitude
                ride.price = rideInfo.price
                ride.comment = rideInfo.comment

                //save ride
                ActiveRide.activeRide = ride

                //create ride with SERVER
                getDriverInteractor.createRide(ride) { isSuccess ->
                    if (isSuccess) {
                        //next step
                        val dateInfo = getDateInfo()
                        if (dateInfo != null) {
                            getDriverInteractor.updateRideStatus(StatusRide.REGULAR_RIDE) { result ->
                                onResponseChangeStatus = result
                                onResponseCreateRide()
                            }

                            getDriverInteractor.createRideSchedule(dateInfo) { result ->
                                onResponseScheduler = result
                                onResponseCreateRide()
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
            }
        }
    }


    private fun onResponseCreateRide() {
        val check = checkCreateResponse.incrementAndGet()
        if (check == 2) {
            if (onResponseChangeStatus && onResponseScheduler) {
                getView()?.finishCreate()
            } else {
                val res = App.appComponent.getContext().resources
                getView()?.showNotification(res.getString(R.string.tryAgain))
            }

            getView()?.hideLoading()

            //reset for next trying
            checkCreateResponse = AtomicInteger(0)
        }
    }


    private fun getDateInfo(): DateInfo? {
        val dateInfo =
            DateInfo()
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
            bankCard?.numberCard,
            bankCard?.validUntil,
            bankCard?.cvc,
            bankCard?.img
        )

        val adapter = getView()?.getPaymentsAdapter()
        adapter?.list?.add(paymentCard)
        adapter?.notifyDataSetChanged()
    }


    override fun startProcessBlockRequest() {
        isBlockGeocoder = false

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