package bonch.dev.view.regulardrive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import bonch.dev.R
import bonch.dev.presenter.regulardrive.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout


class RegularDriveView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.regular_drive_fragment, container, false)

        val scrollView = root.findViewById<View>(R.id.nestedScrollView) as NestedScrollView
        scrollView.isFillViewport = true

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabs)

        tabLayout.setupWithViewPager(viewPager)

        val adapter = ViewPagerAdapter(childFragmentManager)

        adapter.addFragment(ActiveRegularDriveView(), "АКТИВНЫЕ")
        adapter.addFragment(ArchiveRegularDriveView(), "АРХИВИРОВАННЫЕ")

        viewPager.adapter = adapter
    }


}