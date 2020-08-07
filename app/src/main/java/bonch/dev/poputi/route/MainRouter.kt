package bonch.dev.poputi.route

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import bonch.dev.poputi.route.IBaseRouter

object MainRouter : IBaseRouter {

    override fun showView(@IdRes resId: Int, navController: NavController?, args: Bundle?) {
        navController?.navigate(resId, args)
    }

}