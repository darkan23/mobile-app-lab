package labone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.labone.R
import kotlinx.android.synthetic.main.activity_main.*
import splitties.init.appCtx

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val navController: NavController
        get() = findNavController(R.id.navHostFragment)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        bnv_nav?.let { NavigationUI.setupWithNavController(it, navController) }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(
            AppBarConfiguration(navController.graph, DrawerLayout(appCtx))
        ) || super.onSupportNavigateUp()
    }
}
