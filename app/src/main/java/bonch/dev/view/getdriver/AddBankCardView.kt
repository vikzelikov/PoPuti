package bonch.dev.view.getdriver

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import bonch.dev.MainActivity
import bonch.dev.MainActivity.Companion.hideKeyboard
import bonch.dev.R

class AddBankCardView : Fragment() {


    private lateinit var offerBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var cardNumber: EditText
    private lateinit var validUntil: EditText
    private lateinit var cvc: EditText
    private var startHeight: Int = 0
    private var screenHeight: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_bank_card_fragment, container, false)


        initViews(root)

        setListener(root)

        setMovingButtonListener(root)


        return root

    }


    private fun setListener(root:View){
        backBtn.setOnClickListener {
            hideKeyboard(activity!!, root)


            (activity as MainActivity).supportFragmentManager.popBackStack()
        }
    }


    private fun setMovingButtonListener(root: View) {
        var heightDiff: Int
        var btnDefaultPosition = 0.0f

        root.viewTreeObserver
            .addOnGlobalLayoutListener {
                val rect = Rect()

                root.getWindowVisibleDisplayFrame(rect)
                heightDiff = screenHeight - (rect.bottom - rect.top)

                if (screenHeight == 0) {
                    screenHeight = root.rootView.height
                }

                if (btnDefaultPosition == 0.0f) {
                    //init default position of button
                    btnDefaultPosition = offerBtn.y
                }

                if (startHeight == 0) {
                    startHeight = screenHeight - (rect.bottom - rect.top)
                }


                if (heightDiff > startHeight) {
                    //move UP
                    offerBtn.y = btnDefaultPosition - heightDiff + startHeight
                } else {
                    //move DOWN
                    offerBtn.y = btnDefaultPosition
                }
            }
    }


    private fun initViews(root: View){
        offerBtn = root.findViewById(R.id.offer)
        backBtn = root.findViewById(R.id.back_btn)
        cardNumber = root.findViewById(R.id.card_number)
        validUntil = root.findViewById(R.id.valid_until)
        cvc = root.findViewById(R.id.cvc)
    }
}