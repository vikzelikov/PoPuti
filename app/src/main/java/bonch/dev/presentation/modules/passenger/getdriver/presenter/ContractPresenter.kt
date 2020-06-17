package bonch.dev.presentation.modules.passenger.getdriver.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.domain.entities.common.ride.Address
import bonch.dev.domain.entities.passenger.getdriver.*
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractPresenter {

    interface IMapCreateRidePresenter {
        fun instance(): MapCreateRidePresenter
        fun getBitmap(drawableId: Int): Bitmap?
        fun attachCreateRide(fm: FragmentManager)
        fun attachDetailRide(fm: FragmentManager)
        fun getUserLocation(): UserLocationLayer?
        fun requestGeocoder(point: Point?)
        fun isUserCoordinate(args: Bundle?)
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
        fun backEvent()
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
        fun setSelectedBankCard(bankCard: BankCard)
        fun checkAddressPoints(fromAddress: Address, toAddress: Address)
        fun addBankCard(context: Context, fragment: Fragment)
        fun offerPrice(context: Context, fragment: Fragment)
        fun addBankCardDone(data: Intent?)
        fun showRoute()
    }


    interface IMapGetDriverPresenter {
        fun instance(): MapGetDriverPresenter
        fun getBitmap(drawableId: Int): Bitmap?
        fun attachGetDriver(fm: FragmentManager)
        fun attachTrackRide(fm: FragmentManager)
        fun attachRateRide(fm: FragmentManager)
        fun onBackPressed(): Boolean
        fun onObjectUpdated()
    }


    interface IGetDriverPresenter {
        fun cancelDone(reasonID: ReasonCancel)
        fun instance(): GetDriverPresenter
        fun moveCamera(zoom: Float, point: Point)
        fun cancelDoneOtherReason(comment: String)
        fun onUserLocationAttach()
        fun startSearchDrivers()
        fun timeExpired()
        fun confirmAccept()
    }


    interface ITrackRidePresenter {
        fun initTracking()
        fun nextStep(idStep: Int)
        fun setInfoDriver(driver: Driver)
        fun cancelDone(reasonID: ReasonCancel)
        fun backFragment(reasonID: ReasonCancel)
        fun showChat(context: Context, fragment: Fragment)
        fun cancelDoneOtherReason(comment: String)
        fun instance(): TrackRidePresenter
        fun getTaxMoney(): Int
    }

}