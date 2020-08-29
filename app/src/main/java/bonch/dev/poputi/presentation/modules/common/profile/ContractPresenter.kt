package bonch.dev.poputi.presentation.modules.common.profile

import android.app.Activity
import android.content.Intent
import bonch.dev.poputi.domain.entities.common.banking.BankCard
import bonch.dev.poputi.domain.entities.common.profile.verification.VerifyStep
import bonch.dev.poputi.domain.entities.common.ride.Address
import bonch.dev.poputi.domain.entities.common.ride.RideInfo
import bonch.dev.poputi.presentation.modules.common.profile.banking.BankingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.city.SelectCityPresenter
import bonch.dev.poputi.presentation.modules.common.profile.driver.carinfo.CarInfoPresenter
import bonch.dev.poputi.presentation.modules.common.profile.driver.profits.ProfitsPresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.rating.RatingPresenter
import bonch.dev.poputi.presentation.modules.common.profile.passenger.verification.VerifyPresenter
import bonch.dev.poputi.presentation.modules.common.profile.story.presenter.DetailStoryPresenter
import bonch.dev.poputi.presentation.modules.common.profile.story.presenter.StoryPresenter

interface ContractPresenter {

    interface IBankingPresenter {
        fun addCard(activity: Activity)
        fun addBankCardDone(data: Intent?)
        fun instance(): BankingPresenter
        fun getBankCards(): ArrayList<BankCard>
        fun deleteBankCard(card: BankCard)
        fun doneEdit()
        fun editMode()
    }


    interface IVerifyPresenter {
        fun sortDocs()
        fun loadPhoto()
        fun checkVerification()
        fun createVerification()
        fun instance(): VerifyPresenter
        fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?)
        fun getCamera(activity: Activity)
        fun getByValue(step: Int): VerifyStep?
        fun isBlockBack(): Boolean
        fun resetData()
    }


    interface IProfitsPresenter {
        fun nextList()
        fun backList()
        fun calcDate(isNext: Boolean?)
        fun instance(): ProfitsPresenter
    }


    interface IStoryPresenter {
        fun getStory(isForPassenger: Boolean)
        fun onClickItem(rideInfo: RideInfo)
        fun instance(): StoryPresenter
    }


    interface IDetailStoryPresenter {
        fun onReceiveRide(ride: RideInfo)
        fun instance(): DetailStoryPresenter
    }


    interface IRatingPresenter {
        fun getRating()
        fun getProfile()
        fun instance(): RatingPresenter
    }


    interface ISelectCityPresenter {
        fun loadSuggest()
        fun clearSuggest()
        fun filterList(q: String)
        fun instance(): SelectCityPresenter
        fun suggestDone(address: Address)
    }


    interface ICarInfoPresenter {
        fun getCarInfo()
        fun instance(): CarInfoPresenter
    }

}