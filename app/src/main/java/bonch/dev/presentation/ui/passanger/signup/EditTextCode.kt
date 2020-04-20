package bonch.dev.presentation.ui.passanger.signup

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText


/**
 * This is my implementation of EditText
* */

class EditTextCode : EditText {


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)



    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return EditTextInputConnection(
            super.onCreateInputConnection(outAttrs),
            true
        )
    }

    private inner class EditTextInputConnection(target: InputConnection, mutable: Boolean) :
        InputConnectionWrapper(target, mutable) {

        override fun sendKeyEvent(event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                //DELETE pressed
            }
            return super.sendKeyEvent(event)
        }


        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            return if (beforeLength == 1 && afterLength == 0) {
                // backspace
                sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(
                    KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
                )
            } else super.deleteSurroundingText(beforeLength, afterLength)

        }

    }

}