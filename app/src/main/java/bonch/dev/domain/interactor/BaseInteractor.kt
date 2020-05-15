package bonch.dev.domain.interactor

import bonch.dev.App
import bonch.dev.data.storage.common.profile.IProfileStorage
import javax.inject.Inject

class BaseInteractor : IBaseInteractor {

    @Inject
    lateinit var profileStorage: IProfileStorage


    init {
        App.appComponent.inject(this)
    }


    //for authorization
    override fun getToken(): String? {
       return profileStorage.getToken()
    }

}