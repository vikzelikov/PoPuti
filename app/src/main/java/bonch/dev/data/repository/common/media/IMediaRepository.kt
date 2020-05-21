package bonch.dev.data.repository.common.media

import bonch.dev.domain.entities.common.media.MediaObject
import bonch.dev.presentation.interfaces.SuccessHandler
import java.io.File

typealias MediaHandlerData<T> = (data: T, error: String?) -> Unit

interface IMediaRepository {

    fun loadPhoto(token: String, image: File, callback: MediaHandlerData<MediaObject?>)

    fun deletePhoto(token: String, imageId: Int, callback: SuccessHandler)

}