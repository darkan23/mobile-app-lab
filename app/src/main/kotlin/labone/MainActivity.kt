package labone

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.labone.R
import com.example.labone.databinding.ActivityMainBinding
import labone.viewbinding.viewBinding
import splitties.init.appCtx

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val navController: NavController
        get() = findNavController(R.id.navHostFragment)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setContentView(binding.root)
        NavigationUI.setupWithNavController(binding.bnvNav, navController)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(
        AppBarConfiguration(navController.graph, DrawerLayout(appCtx))
    ) || super.onSupportNavigateUp()
}
