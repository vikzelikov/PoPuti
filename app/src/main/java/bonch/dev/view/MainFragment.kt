package bonch.dev.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import bonch.dev.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        val navView: BottomNavigationView = root.findViewById(R.id.nav_view)
        val view: View = root.findViewById(R.id.nav_host_fragment)
        val navController = findNavController(view)

        navView.setupWithNavController(navController)

        return root
    }
}