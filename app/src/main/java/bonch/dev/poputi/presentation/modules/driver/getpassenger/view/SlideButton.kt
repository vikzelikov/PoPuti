package bonch.dev.poputi.presentation.modules.driver.getpassenger.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar


class SlideButton(context: Context, attrs: AttributeSet?) : AppCompatSeekBar(context, attrs) {

    private var anim: Anim? = null
    private var listener: SlideButtonListener? = null
    private var blockHandler: Handler? = null
    private var isBlock = false

    init {
        startProcessBlock()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (thumb.bounds.contains(event.x.toInt(), event.y.toInt())) {
                super.onTouchEvent(event)
            } else {
                if (!isBlock) {
                    LearnAnim().start()
                    isBlock = true
                }
                return false
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (progress > 90) {
                handleSlide()
            }

            if (anim == null) {
                anim = Anim()
            }
            anim?.start()

        } else super.onTouchEvent(event)
        return true
    }


    private fun handleSlide() {
        listener?.handleSlide()
    }


    fun setSlideButtonListener(listener: SlideButtonListener?) {
        this.listener = listener
    }


    inner class Anim : CountDownTimer(1000, 1) {
        override fun onFinish() {}

        override fun onTick(millisUntilFinished: Long) {
            if (progress > 0) progress -= 4
        }
    }


    inner class LearnAnim : CountDownTimer(500, 10) {
        private var isBack = false
        override fun onFinish() {
            progress = 0
        }

        override fun onTick(millisUntilFinished: Long) {
            if (progress > 10) {
                if (!isBack) isBack = true
            }

            if (isBack) progress-- else progress++

        }
    }


    private fun startProcessBlock() {
        if (blockHandler == null) {
            blockHandler = Handler()
        }

        blockHandler?.postDelayed(object : Runnable {
            override fun run() {
                isBlock = false
                blockHandler?.postDelayed(this, 1500)
            }
        }, 0)
    }
}

interface SlideButtonListener {
    fun handleSlide()
}