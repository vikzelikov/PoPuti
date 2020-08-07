package bonch.dev.poputi.data.repository.common.media

import bonch.dev.poputi.domain.entities.common.media.MediaObject
import bonch.dev.poputi.presentation.interfaces.DataHandler
import bonch.dev.poputi.presentation.interfaces.SuccessHandler
import java.io.File

interface IMediaRepository {

    fun loadPhoto(token: String, image: File, callback: DataHandler<MediaObject?>)

    fun deletePhoto(token: String, imageId: Int, callback: SuccessHandler)

}