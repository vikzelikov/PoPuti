package bonch.dev.poputi.presentation.modules.passenger.getdriver.view

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.*
import bonch.dev.poputi.domain.entities.passenger.getdriver.ReasonCancel
import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.interfaces.ParentHandler
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.AddressesAdapter
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.OffersAdapter
import bonch.dev.poputi.presentation.modules.passenger.getdriver.adapters.PaymentsAdapter
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView

interface ContractView {

    interface IMapCreateRideView : IBaseView {
        fun correctMapView()
        fun getFM(): FragmentManager?
        fun getUserLocation(): Point?
        fun zoomMap(cameraPosition: CameraPosition)
        fun getMyCityCall(): ParentHandler<Address>?
        fun zoomMapDistance(cameraPosition: CameraPosition)
        fun moveCamera(point: Point)
        fun addDriverIcon(point: Point): PlacemarkMapObject?
        fun removeDriverIcon()
        fun showUserIcon()
        fun hideUserIcon()
        fun getMap(): MapView
        fun attachCreateRide()
        fun attachDetailRide()
        fun fadeMap()
    }


    interface ICreateRideView : IBaseView {
        fun getAddressesAdapter(): AddressesAdapter
        fun setAddressView(isFrom: Boolean, address: String)
        fun removeAddressesView(isFrom: Boolean)
        fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float)
        fun onClickItem(address: Address, isFromMapSearch: Boolean)
        fun requestGeocoder(cameraPosition: CameraPosition, isUp: Boolean)
        fun getUserLocation(): Point?
        fun onStateChangedBottomSheet(newState: Int)
        fun getMyCityCall(): ParentHandler<Address>?
        fun expandedBottomSheet(isFrom: Boolean)
        fun onBackPressed(): Boolean
        fun moveCamera(point: Point)
        fun getActualFocus(): Boolean?
        fun addressesMapViewChanged()
        fun showDetailRide()
        fun onObjectUpdate()
        fun nextFragment()
        fun showStartUI()
    }


    interface IDetailRideView : IBaseView {
        fun getInfoPrice()
        fun removeRoute()
        fun getMap(): MapView?
        fun removeTickSelected()
        fun hideAllBottomSheet()
        fun onSlideBottomSheet(slideOffset: Float)
        fun onStateChangedBottomSheet(newState: Int)
        fun getPaymentsAdapter(): PaymentsAdapter
        fun setAddresses(fromAddress: String, toAddress: String)
        fun setSelectedBankCard(bankCard: BankCard)
        fun offerPriceDone(price: Int)
        fun isDataComplete(): Boolean
        fun onBackPressed(): Boolean
        fun getRideInfo(): RideInfo
        fun createRideFail()
        fun attachGetOffers()
        fun getPaymentMethods()
        fun commentEditStart()
    }


    interface IGetOffersView : IBaseView {
        fun getOfferFail()
        fun attachTrackRide()
        fun onCancelRide(reason: ReasonCancel)
        fun registerReceivers()
        fun getMap(): MapView?
        fun removeBackground()
        fun getConfirmAccept(offer: Offer)
        fun getExpiredTimeConfirm()
        fun onBackPressed(): Boolean
        fun getAdapter(): OffersAdapter
        fun getUserLocation(): Point?
        fun checkoutBackground(isShow: Boolean)
        fun getRecyclerView(): RecyclerView?
        fun hideConfirmAccept()
    }


    interface ITrackRideView : IBaseView {
        fun setInfoDriver(driver: Driver)
        fun onBackPressed(): Boolean
        fun checkoutIconChat(isShow: Boolean)
        fun checkoutStatusView(idStep: StatusRide)
        fun addDriverIconF(point: Point): PlacemarkMapObject?
        fun rotateDriverIconF(deg: Float)
        fun moveDriverIconF(point: Point)
        fun removeDriverIconF()
        fun onCancelRide(reason: ReasonCancel)
        fun registerReceivers()
        fun getMap(): MapView?
        fun attachRateView()
    }

}