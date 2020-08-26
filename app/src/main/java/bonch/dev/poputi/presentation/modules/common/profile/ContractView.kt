package bonch.dev.poputi.presentation.modules.common.profile

import android.widget.ImageView
import android.widget.TextView
import bonch.dev.poputi.domain.entities.common.media.Photo
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyStep
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.interfaces.IBaseView
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingAdapter
import bonch.dev.poputi.presentation.modules.common.profile.driver.profits.ProfitsAdapter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.story.adapter.StoryAdapter

interface ContractView {

    interface IBankingView : IBaseView {
        fun getAdapter(): BankingAdapter
    }


    interface IVerifyView : IBaseView {
        fun showFullLoading()
        fun hideFullLoading()
        fun setPhoto(step: VerifyStep, img: String)
        fun getImgDocs(): Array<ImageView?>
        fun getTitleDocs(): Array<TextView?>
        fun getTicsDocs(): Array<ImageView?>
        fun setDocs(list: ArrayList<Photo>)
        fun showLoadingPhoto(idDoc: VerifyStep)
        fun hideLoadingPhoto()
        fun finishVerification()
    }


    interface IProfitsView : IBaseView {
        fun getAdapter(): ProfitsAdapter
        fun setDate(time: String)
        fun calcCountOrders()
        fun calcTotalProfit()
        fun showEmptyText()
        fun hideEmptyText()
    }


    interface IStoryView : IBaseView {
        fun showDetailStory()
        fun getAdapter(): StoryAdapter
    }


    interface IDetailStoryView : IBaseView {
        fun setRideInfo(rideInfo: RideInfo)
    }

}