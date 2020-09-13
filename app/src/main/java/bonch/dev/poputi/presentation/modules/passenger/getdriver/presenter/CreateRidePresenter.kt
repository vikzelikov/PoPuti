package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.os.Handler
import android.view.View
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.AddressPoint
import bonch.dev.poputi.domain.entities.common.ride.Coordinate.fromAdr
import bonch.dev.poputi.domain.entities.common.ride.Coordinate.toAdr
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.ride.routing.Routing
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.ContractView
import com.yandex.mapkit.geometry.Point
import javax.inject.Inject


class CreateRidePresenter : BasePresenter<ContractView.ICreateRideView>(),
    ContractPresenter.ICreateRidePresenter {

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    private var blockRequestHandler: Handler? = null

    var isFromMapSearch = true
    var isNextStep = false
    private var isBlockRequest = false

    private var cashEvent: CashEvent
    private val BLOCK_REQUEST_GEOCODER = 1000L


    init {
        GetDriverComponent.getDriverComponent?.inject(this)

        cashEvent = CashEvent(getDriverInteractor)
    }


    override fun addressesDone(): Boolean {
        var isSuccess = false
        val from = fromAdr
        val to = toAdr

        if (from != null && to != null) {
            Routing.mapObjects = null

            getView()?.showDetailRide()

            //cash data (both of adr)
            cashEvent.saveCashSuggest(from)
            cashEvent.saveCashSuggest(to)

            stopProcessBlockRequest()
            isSuccess = true
            isNextStep = true
        }

        return isSuccess
    }


    override fun requestGeocoder(point: Point?) {
        if (!isBlockRequest && point != null && point.latitude != 0.0 && point.longitude != 0.0) {
            //send request and set block for several seconds
            getDriverInteractor.requestGeocoder(point) { address ->

                if (address.address != App.appComponent.getApp().getString(R.string.atlantic)) {

                    address.point?.let {
                        responseGeocoder(address.address, Point(it.latitude, it.longitude))
                    }

                    //callback city
                    getView()?.getMyCityCall()?.let {
                        val a = Address()
                        a.address = address.description
                        a.point = address.point
                        it(a)
                    }

                } else isBlockRequest = false
            }

            isBlockRequest = true
        }
    }


    private fun responseGeocoder(address: String?, point: Point) {
        val actualFocus = getView()?.getActualFocus()
        val place = App.appComponent.getApp().getString(R.string.place)

        if (address != null) {
            if (isFromMapSearch || (actualFocus != null && !actualFocus)) {
                fromAdr = Address(
                    0,
                    address,
                    place,
                    null,
                    AddressPoint(
                        point.latitude,
                        point.longitude
                    )
                )
            } else {
                toAdr = Address(
                    0,
                    address,
                    place,
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
        startProcessBlockRequest()

        if (query.length > 2) {
            getDriverInteractor.initRealm()
            //first check cash, then go to net
            //try to get data from cash
            if (!isBlockRequest) {
                val cashRequest = getDriverInteractor.getCashRequest(query)

                if (!cashRequest.isNullOrEmpty() && !Geo.isPreferCityGeo) {
                    //set in view
                    val adapter = getView()?.getAddressesAdapter()
                    adapter?.list?.clear()
                    adapter?.list?.addAll(cashRequest)
                    adapter?.notifyDataSetChanged()
                    isBlockRequest = true

                } else {
                    getDriverInteractor.requestSuggest(query, getView()?.getUserLocation()) {
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


    override fun onClickItem(address: Address) {
        getView()?.onClickItem(address, isFromMapSearch)

        addressesDone()
    }


    override fun touchCrossFrom(isFrom: Boolean) {
        if (!isFrom) {
            toAdr = null
        }

        getView()?.removeAddressesView(isFrom)
    }


    override fun touchAddress(isFrom: Boolean) {

        getView()?.expandedBottomSheet(isFrom)

        //update current focus
        isFromMapSearch = isFrom
    }


    override fun touchMapBtn(isFrom: Boolean) {
        isFromMapSearch = isFrom

        getView()?.addressesMapViewChanged()
    }


    override fun touchAddressMapMarkerBtn() {
        val res = addressesDone()

        if (!res) {
            getView()?.showStartUI()
        }
    }


    override fun showMyPosition() {
        Geo.isPreferCityGeo = false
        Geo.isRequestUpdateCity = true

        val userPoint = getView()?.getUserLocation()

        if (userPoint != null) {
            getView()?.moveCamera(userPoint)
        }
    }


    override fun startProcessBlockRequest() {
        if (blockRequestHandler == null) {
            blockRequestHandler = Handler()

            blockRequestHandler?.postDelayed(object : Runnable {
                override fun run() {
                    isBlockRequest = false
                    blockRequestHandler?.postDelayed(this, BLOCK_REQUEST_GEOCODER)
                }
            }, 0)
        }
    }


    override fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float) {
        if (slideOffset > 0) {
            getView()?.onSlideBottomSheet(bottomSheet, slideOffset)
        }
    }


    override fun onStateChangedBottomSheet(newState: Int) {
        getView()?.onStateChangedBottomSheet(newState)
    }


    override fun onObjectUpdate() {
        if (blockRequestHandler == null && !isNextStep) {
            startProcessBlockRequest()
        }
    }


    private fun stopProcessBlockRequest() {
        blockRequestHandler?.removeCallbacksAndMessages(null)
        blockRequestHandler = null
        isBlockRequest = true
    }


    override fun onDestroy() {
        cashEvent.cashSuggests = null
        blockRequestHandler = null
        getDriverInteractor.closeRealm()
    }

    override fun instance(): CreateRidePresenter {
        return this
    }
}