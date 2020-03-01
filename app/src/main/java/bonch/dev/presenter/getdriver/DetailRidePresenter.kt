package bonch.dev.presenter.getdriver

import android.content.Context
import androidx.fragment.app.Fragment
import bonch.dev.Constant.Companion.ADD_BANK_CARD_VIEW
import bonch.dev.Constant.Companion.OFFER_PRICE_VIEW
import bonch.dev.Coordinator.Companion.openActivity

class DetailRidePresenter(val context: Context) {

    fun clickOfferPriceBtn(fragment: Fragment) {
        openActivity(OFFER_PRICE_VIEW, context, fragment)
    }


    fun clickAddBankCardBtn(fragment: Fragment) {
        openActivity(ADD_BANK_CARD_VIEW, context, fragment)
    }
}