package bonch.dev

import bonch.dev.view.IBaseView

interface ContractInterface {

    interface IPresenter{
        fun showBottomSheet()
    }

    interface IView: IBaseView{

    }
}