package bonch.dev.poputi.presentation.interfaces

import bonch.dev.poputi.domain.entities.common.ride.Address
import com.yandex.mapkit.geometry.Point

typealias SuccessHandler = (success: Boolean) -> Unit
typealias ParentHandler<T> = (T) -> Unit
typealias ParentEmptyHandler = () -> Unit
typealias ParentMapHandler<T> = () -> T?
typealias DataHandler<T> = (data: T, error: String?) -> Unit

typealias GeocoderHandler = (address: String?, point: Point) -> Unit
typealias SuggestHandler = (suggest: ArrayList<Address>) -> Unit

interface IBasePresenter<V : IBaseView> {

    fun attachView(view: V)

    fun detachView()

    fun isViewAttached(): Boolean

}