package bonch.dev.utils

import android.app.Activity
import android.content.Intent
import bonch.dev.utils.Constants.GALLERY


object Gallery {

    fun getPhoto(activity: Activity) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        activity.startActivityForResult(photoPickerIntent, GALLERY)
    }
}