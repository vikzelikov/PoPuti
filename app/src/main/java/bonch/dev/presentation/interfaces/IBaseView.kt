package bonch.dev.presentation.interfaces

import androidx.navigation.NavController

interface IBaseView {

    fun setListeners()

    fun getNavHost(): NavController

}