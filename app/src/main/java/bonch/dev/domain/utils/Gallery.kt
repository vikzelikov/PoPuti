package bonch.dev.domain.utils

import android.app.Activity
import android.content.Intent
import bonch.dev.domain.utils.Constants.GALLERY


object Gallery {

    fun getPhoto(activity: Activity) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        activity.startActivityForResult(photoPickerIntent, GALLERY)
    }
}