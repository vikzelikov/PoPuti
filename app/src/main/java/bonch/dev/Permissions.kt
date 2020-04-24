package bonch.dev

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import bonch.dev.domain.utils.Constants

class Permissions {

    companion object {

        private val geoPermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        private val storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


        fun access(permission: Int, fragment: Fragment) {
            when (permission) {
                Constants.GEO_PERMISSION_REQUEST -> {
                    fragment.requestPermissions(geoPermission, Constants.GEO_PERMISSION_REQUEST)
                }

                Constants.STORAGE_PERMISSION_REQUEST -> {
                    fragment.requestPermissions(
                        storagePermission,
                        Constants.STORAGE_PERMISSION_REQUEST
                    )
                }
            }
        }


        fun access(permission: Int, activity: Activity) {
            when (permission) {
                Constants.GEO_PERMISSION_REQUEST -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        geoPermission,
                        Constants.GEO_PERMISSION_REQUEST
                    )
                }

                Constants.STORAGE_PERMISSION_REQUEST -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        storagePermission,
                        Constants.STORAGE_PERMISSION_REQUEST
                    )
                }
            }
        }


        fun isAccess(permission: String, activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}