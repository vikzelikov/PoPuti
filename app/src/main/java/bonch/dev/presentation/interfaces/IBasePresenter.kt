package bonch.dev.presentation.interfaces

typealias NotificationHandler = (success: Boolean) -> Unit
typealias ParentHandler<T> = (T) -> Unit
typealias ParentEmptyHandler = () -> Unit
typealias ParentMapHandler<T> = () -> T?

interface IBasePresenter<V : IBaseView> {

    fun attachView(view: V)

    fun detachView()

    fun isViewAttached(): Boolean

}