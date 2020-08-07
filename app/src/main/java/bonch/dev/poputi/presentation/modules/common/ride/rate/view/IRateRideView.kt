package bonch.dev.poputi.presentation.modules.common.ride.rate.view

import bonch.dev.poputi.presentation.interfaces.IBaseView

interface IRateRideView : IBaseView {

    fun isPassanger(): Boolean

    fun onBackPressed(): Boolean

    fun finish(resultCode: Int)

}