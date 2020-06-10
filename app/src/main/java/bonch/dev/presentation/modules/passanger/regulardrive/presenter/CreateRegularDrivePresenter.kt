package bonch.dev.presentation.modules.passanger.regulardrive.presenter

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.data.repository.common.ride.SearchPlace
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.domain.entities.common.ride.Address
import bonch.dev.domain.entities.common.ride.AddressPoint
import bonch.dev.domain.entities.common.ride.Coordinate
import bonch.dev.presentation.base.BasePresenter
import bonch.dev.presentation.modules.common.addbanking.view.AddBankCardView
import bonch.dev.presentation.modules.common.orfferprice.view.OfferPriceView
import bonch.dev.presentation.modules.common.ride.Routing
import bonch.dev.presentation.modules.passanger.regulardrive.RegularDriveComponent
import bonch.dev.presentation.modules.passanger.regulardrive.view.ContractView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import javax.inject.Inject

class CreateRegularDrivePresenter : BasePresenter<ContractView.ICreateRegularDriveView>(),
    ContractPresenter.ICreateRegularDrivePresenter {

    @Inject
    lateinit var routing: Routing

    val OFFER_PRICE = 1
    val ADD_BANK_CARD = 2
    private val AVERAGE_PRICE = "AVERAGE_PRICE"

    private var searchPlace: SearchPlace? = null
    var fromPoint: Point? = null
    var toPoint: Point? = null

    var isFromMapSearch = true



    init {
        RegularDriveComponent.regularDriveComponent?.inject(this)
        //searchPlace = SearchPlace()
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
                searchPlace?.request(fromUri)
            }
        }

        if (toUri != null) {
            if (toUri.length > 50) {
                toPoint = getPoint(toUri)
            } else {
                searchPlace?.request(toUri)
            }
        }

        submitRoute()
    }


    override fun submitRoute() {
        val from = fromPoint
        val to = toPoint
        val map = getMap()

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

        //addressesDone()
    }


    override fun clearSuggest() {
        val adapter = getView()?.getAddressesAdapter()
        adapter?.list?.clear()
        adapter?.notifyDataSetChanged()
    }


    override fun getCashSuggest() {
        //cashEvent.getCashSuggest()
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


    override fun getMap(): MapView? {
        return getView()?.getMap()
    }


    override fun instance(): CreateRegularDrivePresenter {
        return this
    }

}