package bonch.dev.poputi.presentation.modules.common.profile

import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingAdapter

interface ContractView {

    interface IBankingView : IBaseView {
        fun getAdapter():BankingAdapter
    }

}