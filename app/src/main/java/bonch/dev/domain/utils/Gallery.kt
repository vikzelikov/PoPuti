package bonch.dev.domain.utils

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import bonch.dev.domain.utils.Constants.GALLERY


object Gallery {

    //for activity
    fun getPhoto(activity: Activity) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        activity.startActivityForResult(photoPickerIntent, GALLERY)
    }

    //for fragment
    fun getPhoto(fragment: Fragment) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        fragment.startActivityForResult(photoPickerIntent, GALLERY)
    }

}