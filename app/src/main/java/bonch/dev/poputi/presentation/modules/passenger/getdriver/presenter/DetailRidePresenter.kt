package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.data.repository.common.ride.SearchPlace
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.AddressPoint
import bonch.dev.poputi.domain.entities.common.ride.Coordinate.fromAdr
import bonch.dev.poputi.domain.entities.common.ride.Coordinate.toAdr
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.domain.utils.Geo
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.addbanking.view.AddBankCardView
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.view.OfferPriceView
import bonch.dev.poputi.presentation.modules.common.ride.routing.Routing
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.poputi.service.passenger.PassengerRideService
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import javax.inject.Inject

class DetailRidePresenter : BasePresenter<ContractView.IDetailRideView>(),
    ContractPresenter.IDetailRidePresenter {

    @Inject
    lateinit var routing: Routing

    @Inject
    lateinit var getDriverInteractor: IGetDriverInteractor

    val OFFER_PRICE = 1
    val ADD_BANK_CARD = 2

    private var fromPoint: Point? = null
    private var toPoint: Point? = null


    init {
        GetDriverComponent.getDriverComponent?.inject(this)
    }


    override fun offerPrice(context: Context, fragment: Fragment) {
        val intent = Intent(context, OfferPriceView::class.java)
        fragment.startActivityForResult(intent, OFFER_PRICE)
    }


    override fun addBankCard(context: Context, fragment: Fragment) {
        val intent = Intent(context, AddBankCardView::class.java)
        fragment.startActivityForResult(intent, ADD_BANK_CARD)
    }


    override fun checkAddressPoints(fromAddress: Address, toAddress: Address) {
        val fromUri = fromAddress.uri
        val toUri = toAddress.uri
        val fromAdrStr = fromAddress.address
        val toAdrStr = toAddress.address
        val fromP = fromAddress.point
        val toP = toAddress.point

        //set address in view
        if (fromAdrStr != null && toAdrStr != null) {
            getView()?.setAddresses(fromAdrStr, toAdrStr)
        }

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
        val map = getMap()

        if (from != null && to != null && map != null) {
            //update points
            fromAdr?.point = AddressPoint(
                from.latitude,
                from.longitude
            )
            toAdr?.point = AddressPoint(
                to.latitude,
                to.longitude
            )

            routing.submitRequest(from, to, map)
        }
    }


    override fun showRoute() {
        routing.showRoute()
    }


    override fun removeRoute() {
        Routing.removeRoute()
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


    override fun offerPriceDone(data: Intent?) {
        val price = data?.getIntExtra(OFFER_PRICE.toString(), 0)

        if (price != null) {
            getView()?.offerPriceDone(price)
        }
    }


    override fun addBankCardDone(data: Intent?) {
        val bankCard = data?.getParcelableExtra<BankCard>(ADD_BANK_CARD.toString())

        val paymentCard = BankCard(
            1,
            bankCard?.numberCard,
            bankCard?.validUntil,
            bankCard?.cvc
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


    //create ride with SERVER
    override fun createRide() {
        val view = getView()
        val res = App.appComponent.getContext().resources

        if (NetworkUtil.isNetworkConnected(App.appComponent.getContext())) {
            if (view != null && view.isDataComplete()) {
                val rideInfo = getView()?.getRideInfo()

                rideInfo?.fromAdr = fromAdr
                rideInfo?.toAdr = toAdr

                if (rideInfo != null) {

                    getView()?.showLoading()

                    getView()?.attachGetOffers()

                    rideInfo.position = rideInfo.fromAdr?.address
                    rideInfo.fromLat = rideInfo.fromAdr?.point?.latitude
                    rideInfo.fromLng = rideInfo.fromAdr?.point?.longitude
                    rideInfo.destination = rideInfo.toAdr?.address
                    rideInfo.toLat = rideInfo.toAdr?.point?.latitude
                    rideInfo.toLng = rideInfo.toAdr?.point?.longitude
                    rideInfo.city = Geo.selectedCity?.address

                    rideInfo.paymentMethod?.let {
                        getDriverInteractor.saveBankCard(it)
                    }


                    //save ride
                    ActiveRide.activeRide = rideInfo

                    //create ride with SERVER
                    getDriverInteractor.createRide(rideInfo) { isSuccess ->

                        getView()?.hideLoading()

                        if (!isSuccess) {
                            //back step
                            val mainHandler = Handler(Looper.getMainLooper())
                            val myRunnable = Runnable {
                                kotlin.run {
                                    getView()?.showNotification(res.getString(R.string.tryAgain))

                                    ActiveRide.activeRide = null

                                    Handler().postDelayed({
                                        getView()?.createRideFail()
                                    }, 1000)

                                    val app = App.appComponent
                                    app.getApp().stopService(
                                        Intent(
                                            app.getContext(),
                                            PassengerRideService::class.java
                                        )
                                    )
                                }
                            }
                            mainHandler.post(myRunnable)
                        }
                    }
                }
            }
        } else getView()?.showNotification(res.getString(R.string.checkInternet))
    }


    override fun removeTickSelected() {
        getView()?.removeTickSelected()
    }


    override fun setSelectedBankCard(bankCard: BankCard) {
        getView()?.setSelectedBankCard(bankCard)
    }


    override fun getMap(): MapView? {
        return getView()?.getMap()
    }


    override fun onDestroy() {
        fromPoint = null
        toPoint = null
    }


    override fun instance(): DetailRidePresenter {
        return this
    }
}