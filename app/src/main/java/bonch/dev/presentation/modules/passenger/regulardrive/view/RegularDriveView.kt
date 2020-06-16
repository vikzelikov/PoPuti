package bonch.dev.presentation.modules.passenger.regulardrive.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import bonch.dev.App
import bonch.dev.MainActivity
import bonch.dev.R
import bonch.dev.di.component.passenger.DaggerRegularDriveComponent
import bonch.dev.di.module.passenger.RegularDriveModule
import bonch.dev.presentation.modules.passenger.regulardrive.RegularDriveComponent
import bonch.dev.presentation.modules.passenger.regulardrive.adapters.ViewPagerAdapter
import bonch.dev.presentation.modules.passenger.regulardrive.presenter.ContractPresenter
import kotlinx.android.synthetic.main.regular_drive_fragment.*
import javax.inject.Inject


class RegularDriveView : Fragment(), ContractView.IRegularDriveView {

    @Inject
    lateinit var regularDrivePresenter: ContractPresenter.IRegularDrivePresenter


    init {
        initDI()

        RegularDriveComponent.regularDriveComponent?.inject(this)

        regularDrivePresenter.instance().attachView(this)
    }


    //first build DI component
    private fun initDI() {
        if (RegularDriveComponent.regularDriveComponent == null) {
            RegularDriveComponent.regularDriveComponent = DaggerRegularDriveComponent
                .builder()
                .regularDriveModule(RegularDriveModule())
                .appComponent(App.appComponent)
                .build()
        }
    }


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

        setListeners()

        setViewPager()
    }


    private fun setViewPager(){
        val adapter = ViewPagerAdapter(childFragmentManager)

        tabs.setupWithViewPager(viewPager)

        adapter.addFragment(ActiveRegularDriveView(), getString(R.string.ACTIVE))
        adapter.addFragment(ArchiveRegularDriveView(), getString(R.string.ARCHIVE))

        viewPager.adapter = adapter
    }


    override fun setListeners() {
        create_regular_ride.setOnClickListener {
            regularDrivePresenter.createRegularDrive()
        }
    }


    override fun getFragment(): Fragment {
        return this
    }


    override fun getNavHost(): NavController? {
        return (activity as? MainActivity)?.getNavHost()
    }


    override fun hideKeyboard() {}


    override fun showNotification(text: String) {
        (activity as? MainActivity)?.showNotification(text)
    }


    override fun showLoading() {
        (activity as? MainActivity)?.showLoading()
    }


    override fun hideLoading() {
        (activity as? MainActivity)?.hideLoading()
    }


}