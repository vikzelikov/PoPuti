package bonch.dev.presentation.base

import bonch.dev.presentation.interfaces.IBasePresenter
import bonch.dev.presentation.interfaces.IBaseView

open class BasePresenter<V : IBaseView> : IBasePresenter<V> {

    private var mView: V? = null

    override fun attachView(view: V) {
        mView = view
    }


    override fun detachView() {
        if (mView != null) mView = null
    }


    override fun isViewAttached(): Boolean {
        return mView != null
    }


    fun getView(): V? {
        return mView
    }


    fun checkViewAttached() {
        if (!isViewAttached()) throw ViewNotAttachedException()
    }


    class ViewNotAttachedException : RuntimeException("Please call Presenter.attachView(view) before" + " requesting data to the Presenter")

}