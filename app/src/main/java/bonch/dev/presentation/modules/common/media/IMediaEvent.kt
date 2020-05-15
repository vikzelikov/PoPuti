package bonch.dev.presentation.modules.common.media

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface IMediaEvent {

    fun convertImage(bitmap: Bitmap, name: String): File?

    fun getBitmap(uri: Uri): Bitmap

    fun getOrientation(uri: Uri): String?

}