package bonch.dev.presentation.interfaces

typealias SuccessHandler = (success: Boolean) -> Unit
typealias ErrorHandler = (error: String?) -> Unit
typealias ParentHandler<T> = (T) -> Unit
typealias ParentEmptyHandler = () -> Unit
typealias ParentMapHandler<T> = () -> T?
typealias DataHandler<T> = (data: T, error: String?) -> Unit

interface IBasePresenter<V : IBaseView> {

    fun attachView(view: V)

    fun detachView()

    fun isViewAttached(): Boolean

}