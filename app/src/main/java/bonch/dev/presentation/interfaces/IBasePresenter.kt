package bonch.dev.presentation.interfaces

typealias NotificationHandler = (success: Boolean) -> Unit

interface IBasePresenter<V: IBaseView> {

    fun attachView(view: V)

    fun detachView()

    fun isViewAttached(): Boolean
}