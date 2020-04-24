package bonch.dev.presentation.interfaces

interface IBasePresenter<V: IBaseView> {

    fun attachView(view: V)

    fun detachView()

    fun isViewAttached(): Boolean
}