package bonch.dev.view.passanger.signup

import android.text.TextPaint
import android.text.style.URLSpan

//remove underline of links in TextView terms
class URLNoUnderline(url: String) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}