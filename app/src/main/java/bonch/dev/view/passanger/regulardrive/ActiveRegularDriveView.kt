package bonch.dev.view.passanger.regulardrive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.R

class ActiveRegularDriveView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.active_regular_drive_fragment, container, false)


        return root
    }
}