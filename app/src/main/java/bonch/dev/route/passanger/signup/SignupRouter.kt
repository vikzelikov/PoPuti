package bonch.dev.route.passanger.signup

import androidx.navigation.NavController
import bonch.dev.R

class SignupRouter : ISignupRouter {

    override fun showConfirmPhoneView(navController: NavController?) {
        navController?.navigate(R.id.show_confirm_phone_view)
    }


    override fun showFullNameView(navController: NavController?) {
        navController?.navigate(R.id.show_full_name_view)
    }


    override fun showMainFragment(navController: NavController?) {
        navController?.navigate(R.id.show_main_passanger_fragment)
    }

}