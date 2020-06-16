package bonch.dev.data.repository.common.media

import bonch.dev.domain.entities.common.media.MediaObject
import bonch.dev.presentation.interfaces.DataHandler
import bonch.dev.presentation.interfaces.SuccessHandler
import java.io.File

interface IMediaRepository {

    fun loadPhoto(token: String, image: File, callback: DataHandler<MediaObject?>)

    fun deletePhoto(token: String, imageId: Int, callback: SuccessHandler)

}