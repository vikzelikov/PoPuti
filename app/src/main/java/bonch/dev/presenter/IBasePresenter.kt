package bonch.dev.presenter

import bonch.dev.view.IBaseView

interface IBasePresenter<V: IBaseView> {

    fun attachView(view: V)

    fun detachView()

    fun isViewAttached(): Boolean
}