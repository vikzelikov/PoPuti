package bonch.dev.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

object Keyboard {
    private fun getKeyboard(activity: FragmentActivity): InputMethodManager {
        return activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    fun showKeyboard(activity: FragmentActivity) {
        getKeyboard(activity).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(activity: FragmentActivity, root: View) {
        getKeyboard(activity).hideSoftInputFromWindow(root.windowToken, 0)
    }
}