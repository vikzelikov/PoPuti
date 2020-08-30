package bonch.dev.poputi.presentation.modules.driver.rating.presenter

import android.os.Handler
import android.os.Looper
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.interactor.driver.rating.IRatingInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.driver.rating.RatingComponent
import bonch.dev.poputi.presentation.modules.driver.rating.view.IRatingView
import javax.inject.Inject

class RatingPresenter : BasePresenter<IRatingView>(), IRatingPresenter {

    @Inject
    lateinit var interactor: IRatingInteractor


    init {
        RatingComponent.ratingComponent?.inject(this)
    }


    override fun getProfile() {
        val context = App.appComponent.getContext()
        if (!NetworkUtil.isNetworkConnected(context)) {
            getView()?.showNotification(context.resources.getString(R.string.checkInternet))
        }

        val p = CacheProfile.profile

        if (p != null) getView()?.setProfile(p)
        else {
            interactor.getProfile { profileData, _ ->
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    kotlin.run {
                        if (profileData?.firstName == null) {
                            Handler().postDelayed({
                                getProfile()
                            }, 1000)
                        }

                        profileData?.let {
                            getView()?.setProfile(it)
                        }
                    }
                }
                mainHandler.post(myRunnable)
            }
        }
    }

    override fun getRating() {
        interactor.getRating { rating, _ ->
            if (rating.isNullOrEmpty()) {
                getView()?.showEmptyText()

            } else {
                if (rating.size < 2) {
                    getView()?.showEmptyText()

                } else {
                    getView()?.hideEmptyText()

                    rating.sortBy {
                        it.rating
                    }

                    getView()?.getAdapter()?.list?.let {
                        if (it.isEmpty()) {
                            getView()?.getAdapter()?.list?.addAll(rating)
                            getView()?.getAdapter()?.notifyDataSetChanged()
                        }
                    }
                }
            }

            getView()?.hideLoading()
        }
    }


    override fun instance() = this

}