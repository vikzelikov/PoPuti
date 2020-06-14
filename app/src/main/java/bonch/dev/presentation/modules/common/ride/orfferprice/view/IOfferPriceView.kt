package bonch.dev.presentation.modules.common.ride.orfferprice.view

import android.text.Editable
import bonch.dev.presentation.interfaces.IBaseView

interface IOfferPriceView : IBaseView {

    fun setAveragePrice(averagePrice: Int?)

    fun maskListener(s: Editable)

    fun touchPriceEditText()

}