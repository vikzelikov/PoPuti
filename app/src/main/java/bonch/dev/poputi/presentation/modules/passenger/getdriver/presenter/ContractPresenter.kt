package bonch.dev.poputi.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.Offer
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.entities.passenger.getdriver.ReasonCancel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

interface ContractPresenter {

    interface IMapCreateRidePresenter {
        fun instance(): MapCreateRidePresenter
        fun getBitmap(drawableId: Int): Bitmap?
        fun attachCreateRide(fm: FragmentManager)
        fun requestGeocoder(cameraPosition: CameraPosition, isUp: Boolean)
        fun getUserLocation(): Point?
        fun saveMyCity(address: Address)
        fun getMyCity(): Address?
        fun onObjectUpdate()
    }


    interface ICreateRidePresenter {
        fun getCashSuggest()
        fun requestGeocoder(point: Point?)
        fun instance(): CreateRidePresenter
        fun onSlideBottomSheet(bottomSheet: View, slideOffset: Float)
        fun onStateChangedBottomSheet(newState: Int)
        fun onClickItem(address: Address)
        fun touchCrossFrom(isFrom: Boolean)
        fun requestSuggest(query: String)
        fun touchAddress(isFrom: Boolean)
        fun touchMapBtn(isFrom: Boolean)
        fun touchAddressMapMarkerBtn()
        fun startProcessBlockRequest()
        fun addressesDone(): Boolean
        fun onObjectUpdate()
        fun showMyPosition()
        fun clearSuggest()
        fun onDestroy()
    }


    interface IDetailRidePresenter {
        fun submitRoute()
        fun removeRoute()
        fun createRide()
        fun getMap(): MapView?
        fun removeTickSelected()
        fun offerPriceDone(data: Intent?)
        fun instance(): DetailRidePresenter
        fun getBankCards(): ArrayList<BankCard>
        fun setSelectedBankCard(bankCard: BankCard)
        fun checkAddressPoints(fromAddress: Address, toAddress: Address)
        fun addBankCard(context: Context, fragment: Fragment)
        fun offerPrice(context: Context, fragment: Fragment)
        fun addBankCardDone(data: Intent?)
        fun onDestroy()
        fun showRoute()
    }


    interface IGetOffersPresenter {
        fun instance(): GetOffersPresenter
        fun backFragment(reasonID: ReasonCancel)
        fun cancelDone(reasonID: ReasonCancel, textReason: String)
        fun cancelDoneOtherReason(comment: String?)
        fun timeExpired(textReason: String)
        fun getOffers(): ArrayList<Offer>
        fun deleteOffer(offerId: Int)
        fun setOffer(offer: Offer)
        fun getOffer(): Offer?
        fun checkOnOffers()
        fun startSearchAnimation()
        fun registerReceivers()
        fun confirmAccept()
        fun mainTimer()
    }


    interface ITrackRidePresenter {
        fun route()
        fun showRoute()
        fun clearData()
        fun removeRoute()
        fun registerReceivers()
        fun nextStep(idStep: Int)
        fun cancelDone(reasonID: ReasonCancel, textReason: String)
        fun backFragment(reasonID: ReasonCancel)
        fun showChat(context: Context, fragment: Fragment)
        fun cancelDoneOtherReason(comment: String?)
        fun getByValue(status: Int?): StatusRide?
        fun instance(): TrackRidePresenter
        fun getTaxMoney(): Int
    }

}