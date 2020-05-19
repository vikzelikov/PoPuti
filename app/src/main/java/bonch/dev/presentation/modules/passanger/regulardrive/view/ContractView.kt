package bonch.dev.presentation.modules.passanger.regulardrive.view

import androidx.fragment.app.Fragment
import bonch.dev.presentation.interfaces.IBaseView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

interface ContractView {

    interface IRegularDriveView : IBaseView {
        fun getFragment(): Fragment
    }


    interface ICreateRegularDriveView : IBaseView {
        fun getUserLocation(): UserLocationLayer?
        fun getMap(): MapView
        fun moveCamera(point: Point)
    }

}