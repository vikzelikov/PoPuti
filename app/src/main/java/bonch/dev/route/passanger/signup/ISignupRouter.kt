package bonch.dev.route.passanger.signup

import androidx.navigation.NavController

interface ISignupRouter {

    fun showConfirmPhoneView(navController: NavController?)

    fun showFullNameView(navController: NavController?)

    fun showMainFragment(navController: NavController?)

}