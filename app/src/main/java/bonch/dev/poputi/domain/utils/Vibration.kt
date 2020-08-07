package bonch.dev.domain.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object Vibration {
    fun start(context: Context) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}