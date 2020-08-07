package bonch.dev.domain.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import bonch.dev.domain.utils.Constants.CAMERA


object Camera {

    //for activity
    fun getCamera(activity: Activity): Uri? {
        val imageUri = activity.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues()
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activity.startActivityForResult(intent, CAMERA)

        return imageUri
    }


    //for fragment
    fun getCamera(fragment: Fragment): Uri? {
        val imageUri = fragment.context?.contentResolver?.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues()
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        fragment.startActivityForResult(intent, CAMERA)

        return imageUri
    }

}