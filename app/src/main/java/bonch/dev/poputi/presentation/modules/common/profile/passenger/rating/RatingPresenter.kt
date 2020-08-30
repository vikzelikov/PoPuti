package bonch.dev.poputi.presentation.modules.common.profile.passenger.rating

import android.os.Handler
import bonch.dev.poputi.domain.utils.NetworkUtil
import bonch.dev.poputi.App
import bonch.dev.poputi.R
import bonch.dev.poputi.domain.entities.common.profile.CacheProfile
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject

class RatingPresenter : BasePresenter<ContractView.IRatingView>(),
    ContractPresenter.IRatingPresenter {

    @Inject
    lateinit var interactor: IProfileInteractor

    init {
        ProfileComponent.profileComponent?.inject(this)
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
                profileData?.let {
                    getView()?.setProfile(it)
                }
            }
        }
    }


    override fun getRating() {
        interactor.getRating { rating, _ ->
            if (rating.isNullOrEmpty()) {
                getView()?.showEmptyText()

            } else {
                if (rating.size < 15) {
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

            Handler().postDelayed({
                getView()?.hideLoading()
            }, 1000)
        }
    }


    override fun instance() = this
}