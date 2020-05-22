package bonch.dev.presentation.modules.common.media

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import bonch.dev.App
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception

class MediaEvent : IMediaEvent {

    override fun convertImage(bitmap: Bitmap, name: String): File? {
        val context = App.appComponent.getContext()
        val filesDir: File = context.filesDir
        var file: File?
        val os: OutputStream

        try {
            file = File(filesDir, "$name.jpg")
            os = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            file = null
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }

        return file
    }


    override fun getBitmap(uri: Uri): Bitmap? {
        val contentResolver = App.appComponent.getContext().contentResolver

        try {
            return if (Build.VERSION.SDK_INT >= 29) {
                // To handle deprication use
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        contentResolver,
                        uri
                    )
                )
            } else {
                // Use older version
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        }catch (ex: Exception){
            return null
        }
    }


    override fun getOrientation(uri: Uri): String? {
        //get orientation
        val contentResolver = App.appComponent.getContext().contentResolver
        val inputStream = contentResolver.openInputStream(uri)

        return if (inputStream != null) {
            val oldExif = ExifInterface(inputStream)
            oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)
        } else {
            null
        }
    }

}