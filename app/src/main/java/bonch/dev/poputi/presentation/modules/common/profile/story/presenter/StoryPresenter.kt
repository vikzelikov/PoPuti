package bonch.dev.poputi.presentation.modules.common.profile.story.presenter

import android.os.Handler
import bonch.dev.poputi.domain.entities.common.ride.ActiveRide
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.domain.entities.common.ride.StatusRide
import bonch.dev.poputi.domain.interactor.common.profile.IProfileInteractor
import bonch.dev.poputi.presentation.base.BasePresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractPresenter
import bonch.dev.poputi.presentation.modules.common.profile.ContractView
import bonch.dev.presentation.modules.common.profile.ProfileComponent
import javax.inject.Inject

class StoryPresenter : BasePresenter<ContractView.IStoryView>(),
    ContractPresenter.IStoryPresenter {

    @Inject
    lateinit var profileInteractor: IProfileInteractor


    init {
        ProfileComponent.profileComponent?.inject(this)
    }


    override fun getStory(isForPassenger: Boolean) {
        if (isForPassenger) {
            profileInteractor.getStoryRidesPassenger { story, _ ->
                onResponseStory(story)
            }
        } else {
            profileInteractor.getStoryRidesDriver { story, _ ->
                onResponseStory(story)
            }
        }
    }


    private fun onResponseStory(story: ArrayList<RideInfo>?) {
        if (story.isNullOrEmpty()) {
            Handler().postDelayed({
                getView()?.hideLoading()

                getView()?.showEmptyRidesText()
            }, 1000)
        } else {
            val resultStory = arrayListOf<RideInfo>()

            story.forEach {
                if (it.driver != null && it.statusId == StatusRide.GET_PLACE.status)
                    resultStory.add(it)
            }

            resultStory.sortByDescending {
                it.rideId
            }

            getView()?.hideLoading()

            if (resultStory.isNotEmpty()) {
                getView()?.getAdapter()?.list?.addAll(resultStory)
                getView()?.getAdapter()?.notifyDataSetChanged()

            } else getView()?.showEmptyRidesText()
        }
    }


    override fun onClickItem(rideInfo: RideInfo) {
        ActiveRide.activeRide = rideInfo

        getView()?.showDetailStory()
    }


    override fun instance() = this

}