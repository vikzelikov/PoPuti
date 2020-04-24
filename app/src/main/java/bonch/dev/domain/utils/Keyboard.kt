package bonch.dev.domain.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.offer_price_activity.view.*

object Keyboard {
    private fun getKeyboard(activity: FragmentActivity): InputMethodManager {
        return activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }


    fun showKeyboard(activity: FragmentActivity) {
        getKeyboard(activity).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


    fun hideKeyboard(activity: FragmentActivity, root: View?) {
        getKeyboard(activity).hideSoftInputFromWindow(root?.windowToken, 0)
    }


    fun setMovingButtonListener(root: View, isStartHide: Boolean) {
        val button = root.btn_done
        var heightDiff = 0
        var btnDefaultPosition = 0.0f
        val rect = Rect()
        var startHeight = 0
        var screenHeight = 0
        var isStart = isStartHide


        root.viewTreeObserver
            .addOnGlobalLayoutListener {

                root.getWindowVisibleDisplayFrame(rect)
                heightDiff = screenHeight - (rect.bottom - rect.top)

                if (screenHeight == 0) {
                    screenHeight = root.rootView.height
                }

                if (btnDefaultPosition == 0.0f) {
                    //init default position of button
                    btnDefaultPosition = button.y
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }

                if (heightDiff > startHeight) {
                    if(isStart){
                        button.y = btnDefaultPosition - heightDiff + startHeight
                    }
                    isStart = true
                } else {
                    //move DOWN
                    button.y = btnDefaultPosition
                }
            }
    }
}