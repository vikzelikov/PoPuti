package bonch.dev

import bonch.dev.presentation.ui.IBaseView

interface ContractInterface {

    interface IPresenter{
        fun showBottomSheet()
    }

    interface IView: IBaseView {

    }
}