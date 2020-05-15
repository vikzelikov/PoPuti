package bonch.dev.route

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController

interface IBaseRouter {

    fun showView(@IdRes resId: Int, navController: NavController?, args: Bundle?)

}