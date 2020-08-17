package bonch.dev.poputi.presentation.modules.passenger.regular.ride.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bonch.dev.poputi.R

class ArchiveRidesView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.archive_regular_rides_fragment, container, false)


        return root
    }
}