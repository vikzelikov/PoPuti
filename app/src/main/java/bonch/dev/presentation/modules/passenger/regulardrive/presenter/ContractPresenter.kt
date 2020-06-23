package bonch.dev.presentation.modules.passenger.regulardrive.presenter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import bonch.dev.domain.entities.common.banking.BankCard
import bonch.dev.domain.entities.common.ride.Address
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractPresenter {

    interface IRegularDrivePresenter {
        fun instance(): RegularDrivePresenter
        fun createRegularDrive()
    }


    interface IMapCreateRegDrivePresenter {
        fun instance(): MapCreateRegDrivePresenter
        fun attachCreateRegularDrive(fm: FragmentManager)
        fun getBitmap(drawableId: Int): Bitmap?
        fun getUserLocation(): UserLocationLayer?
        fun requestGeocoder(point: Point?)
    }


    interface ICreateRegularDrivePresenter {
        fun instance(): CreateRegularDrivePresenter
        fun getMap(): MapView?
        fun setSelectedBankCard(bankCard: BankCard)
        fun removeTickSelected()
        fun addBankCard(context: Context, fragment: Fragment)
        fun checkAddressPoints(fromAddress: Address, toAddress: Address)
        fun offerPrice(context: Context, fragment: Fragment)
        fun addBankCardDone(data: Intent?)
        fun onClickItem(address: Address)
        fun offerPriceDone(data: Intent?)
        fun getCashSuggest()
        fun clearSuggest()
        fun removeRoute()
        fun submitRoute()
        fun showRoute()
    }

}