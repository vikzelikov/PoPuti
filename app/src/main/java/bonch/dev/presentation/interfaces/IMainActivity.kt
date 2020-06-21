package bonch.dev.presentation.interfaces

import androidx.fragment.app.FragmentManager

interface IMainActivity : IBaseView {

    fun pressBack()

    fun getFM(): FragmentManager

    fun finishActivity()

    fun showFullLoading()

    fun hideFullLoading()
}