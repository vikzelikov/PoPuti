package bonch.dev

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bonch.dev.utils.Constants

class Permissions {

    companion object {

        fun access(permission: Int, activity: Activity) {
            when (permission) {
                Constants.LOCATION_PERMISSION_REQUEST -> {

                    accessUserGeo(activity)
                    blockUI(Constants.LOCATION_PERMISSION_NAME, activity)

                }

                Constants.WRITE_EXTERNAL_STORAGE_REQUEST -> {

                    accessWriteStorage(activity)
                    blockUI(Constants.WRITE_EXTERNAL_STORAGE, activity)

                }
            }
        }


        private fun accessUserGeo(activity: Activity) {
            if (ContextCompat.checkSelfPermission(activity, Constants.LOCATION_PERMISSION_NAME)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Constants.LOCATION_PERMISSION_NAME),
                    Constants.LOCATION_PERMISSION_REQUEST
                )
            }
        }


        private fun accessWriteStorage(activity: Activity) {
            if (ContextCompat.checkSelfPermission(activity, Constants.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Constants.WRITE_EXTERNAL_STORAGE),
                    Constants.WRITE_EXTERNAL_STORAGE_REQUEST
                )
            }
        }


        private fun blockUI(permission: String, activity: Activity) {
            while (true) {
                if (ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    break
                }
            }
        }
    }
}