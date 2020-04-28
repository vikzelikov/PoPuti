package bonch.dev.domain.interactor.passanger.getdriver

import bonch.dev.data.repository.passanger.getdriver.IGetDriverRepository
import bonch.dev.data.storage.passanger.getdriver.IGetDriverStorage
import javax.inject.Inject

class GetDriverInteractor : IGetDriverInteractor {

    @Inject
    lateinit var getDriverRepository: IGetDriverRepository

    @Inject
    lateinit var getDriverStorage: IGetDriverStorage



}