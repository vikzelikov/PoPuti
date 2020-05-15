package bonch.dev

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class Permissions {

    companion object {

        const val GEO_PERMISSION = "android.permission.ACCESS_FINE_LOCATION"
        const val STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"
        const val GEO_PERMISSION_REQUEST = 1
        const val STORAGE_PERMISSION_REQUEST = 2

        private val geoPermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        private val storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


        fun access(permission: Int, fragment: Fragment) {
            when (permission) {
                GEO_PERMISSION_REQUEST -> {
                    fragment.requestPermissions(geoPermission, GEO_PERMISSION_REQUEST)
                }

                STORAGE_PERMISSION_REQUEST -> {
                    fragment.requestPermissions(
                        storagePermission,
                        STORAGE_PERMISSION_REQUEST
                    )
                }
            }
        }


        fun access(permission: Int, activity: Activity) {
            when (permission) {
                GEO_PERMISSION_REQUEST -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        geoPermission,
                        GEO_PERMISSION_REQUEST
                    )
                }

                STORAGE_PERMISSION_REQUEST -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        storagePermission,
                        STORAGE_PERMISSION_REQUEST
                    )
                }
            }
        }


        fun isAccess(permission: String, context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}