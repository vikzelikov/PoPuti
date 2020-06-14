package bonch.dev.presentation.modules.passenger.signup.view

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.core.content.ContextCompat

/**
 * Custom EditText for input code
 * */


@SuppressLint("AppCompatCustomView")
class PinEntryEditText : EditText {
    private val XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android"

    private var mSpace = 24f //24 dp by default, space between the lines

    private var mCharSize = 0f
    private var mNumChars = 4f
    private var mLineSpacing = 12f //8dp by default, height of the text from our lines

    private var mMaxLength = 4

    private var mClickListener: OnClickListener? = null

    private var mLineStroke = 1f //1dp by default

    private var mLineStrokeSelected = 2f //2dp by default

    private var mLinesPaint: Paint? = null
    private var mStates = arrayOf(
        intArrayOf(R.attr.state_selected),
        intArrayOf(R.attr.state_focused),
        intArrayOf(-R.attr.state_focused)
    )

    private var mColors = intArrayOf(
        ContextCompat.getColor(context, bonch.dev.R.color.colorBlue),
        ContextCompat.getColor(context, bonch.dev.R.color.normalStateET),
        Color.GRAY
    )

    private var mColorStates = ColorStateList(mStates, mColors)


    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val multi: Float = context.resources.displayMetrics.density
        mLineStroke *= multi
        mLineStrokeSelected *= multi
        mLinesPaint = Paint(paint)
        mLinesPaint?.strokeWidth = mLineStroke

        setBackgroundResource(0)
        mSpace *= multi //convert to pixels for our density
        mLineSpacing *= multi //convert to pixels for our density
        mMaxLength = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", 4)
        mNumChars = mMaxLength.toFloat()
        //Disable copy paste
        super.setCustomSelectionActionModeCallback(object : android.view.ActionMode.Callback {
            override fun onActionItemClicked(
                mode: android.view.ActionMode?,
                item: MenuItem?
            ): Boolean {
                return false
            }

            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {}
        })
        // When tapped, move cursor to end of text.
        super.setOnClickListener { v ->
            setSelection(text.length)
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: android.view.ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) { //super.onDraw(canvas);
        val availableWidth = width - paddingRight - paddingLeft
        mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }
        var startX = paddingLeft.toFloat()
        val bottom = height - paddingBottom
        //Text Width
        val text = text
        val textLength = text.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)


        var i = 0
        while (i < mNumChars) {
            updateColorForLines(i == textLength)
            if (mLinesPaint != null) {
                canvas.drawLine(
                    startX,
                    bottom.toFloat(),
                    startX + mCharSize,
                    bottom.toFloat(),
                    mLinesPaint!!
                )
            }
            if (getText().length > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(
                    text,
                    i,
                    i + 1,
                    middle - textWidths[0] / 2,
                    bottom - mLineSpacing,
                    paint
                )
            }
            startX += if (mSpace < 0) {
                (mCharSize * 2).toInt()
            } else {
                (mCharSize + mSpace.toInt()).toInt()
            }
            i++
        }
    }


    private fun getColorForState(vararg states: Int): Int {
        return mColorStates.getColorForState(states, Color.GRAY)
    }

    /**
     * @param next Is the current char the next character to be input?
     */
    private fun updateColorForLines(next: Boolean) {
        if (isFocused) {
            mLinesPaint?.strokeWidth = mLineStrokeSelected
            mLinesPaint?.color = getColorForState(R.attr.state_focused)
            if (next) {
                mLinesPaint?.color = getColorForState(R.attr.state_selected)
            }
        } else {
            mLinesPaint?.strokeWidth = mLineStroke
            mLinesPaint?.color = getColorForState(-R.attr.state_focused)
        }
    }
}