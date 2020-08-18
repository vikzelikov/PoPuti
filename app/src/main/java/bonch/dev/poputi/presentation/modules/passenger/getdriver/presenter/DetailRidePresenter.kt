package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.content.Intent
import android.os.Handler
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
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.interactor.passenger.getdriver.IGetDriverInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.addbanking.view.AddBankCardView
import bonch.dev.poputi.presentation.modules.common.ride.orfferprice.view.OfferPriceView
import bonch.dev.poputi.presentation.modules.common.ride.routing.Routing
import bonch.dev.poputi.presentation.modules.passenger.getdriver.GetDriverComponent
import bonch.dev.poputi.presentation.modules.passenger.getdriver.view.ContractView
import bonch.dev.poputi.route.MainRouter
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
    private val AVERAGE_PRICE = "AVERAGE_PRICE"

    private var blockHandler: Handler? = null

    private var isBlock = false

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

            routing.submitRequest(from, to, true, map)
        }
    }


    override fun showRoute() {
        routing.showRoute()
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


    //create ride with SERVER
    override fun createRide() {
        val view = getView()

        if (view != null && view.isDataComplete() && !isBlock) {
            val rideInfo = getView()?.getRideInfo()

            getView()?.showLoading()

            isBlock = true

            rideInfo?.fromAdr = fromAdr
            rideInfo?.toAdr = toAdr

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

                    getView()?.hideLoading()

                    if (isSuccess) {
                        //next step
                        MainRouter.showView(
                            R.id.show_get_driver_fragment,
                            getView()?.getNavHost(),
                            null
                        )
                    } else {
                        ActiveRide.activeRide = null

                        val res = App.appComponent.getContext().resources
                        getView()?.showNotification(res.getString(R.string.errorSystem))
                    }
                }
            }
        }
    }


    override fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 3500)
            }
        }, 0)
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
        blockHandler?.removeCallbacksAndMessages(null)
    }


    override fun instance(): DetailRidePresenter {
        return this
    }
}