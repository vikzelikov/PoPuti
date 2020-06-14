package bonch.dev.presentation.modules.common.ride.rate.view

import bonch.dev.presentation.interfaces.IBaseView

interface IRateRideView : IBaseView {

    fun showNotification(text: String)

    fun isPassanger(): Boolean

    fun onBackPressed(): Boolean

    fun finish(resultCode: Int)

}