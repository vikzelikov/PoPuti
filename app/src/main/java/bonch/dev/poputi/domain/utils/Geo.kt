package bonch.dev.poputi.domain.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import bonch.dev.poputi.domain.entities.common.ride.Address
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

object Geo {

    /**
     * Case when the user manually changes their real location
     * */
    var isPreferCityGeo = false

    var isRequestMyPosition = false

    private var googleApiClient: GoogleApiClient? = null

    var selectedCity: Address? = null

    const val SELECT_CITY = 13
    private const val REQUESTLOCATION = 199

    fun isEnabled(context: Context): Boolean? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            val mode = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )

            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }


    fun showAlertEnable(activity: Activity) {
        googleApiClient = GoogleApiClient.Builder(activity)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(bundle: Bundle?) {}
                override fun onConnectionSuspended(i: Int) {
                    googleApiClient?.connect()
                }
            }).addOnConnectionFailedListener {}.build()

        googleApiClient?.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 30 * 1000.toLong()
        locationRequest.fastestInterval = 5 * 1000.toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        LocationServices.SettingsApi
            .checkLocationSettings(googleApiClient, builder.build())
            .setResultCallback { resultCallback ->
                val status: Status = resultCallback.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        status.startResolutionForResult(
                            activity,
                            REQUESTLOCATION
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    }
                }
            }
    }

}