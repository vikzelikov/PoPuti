package bonch.dev.presentation.presenter

import bonch.dev.presentation.ui.IBaseView

interface IBasePresenter<V: IBaseView> {

    fun attachView(view: V)

    fun detachView()

    fun isViewAttached(): Boolean
}