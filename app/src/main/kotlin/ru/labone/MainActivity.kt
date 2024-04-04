package ru.labone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.labone.R
import com.example.labone.databinding.ActivityMainBinding
import ru.labone.viewbinding.viewBinding
import splitties.init.appCtx

class MainActivity : AppCompatActivity(), ActionBarTitleChanger {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val navController: NavController
        get() = findNavController(R.id.navHostFragment)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMain)
        NavigationUI.setupWithNavController(binding.bnvNav, navController)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(
        AppBarConfiguration(navController.graph, DrawerLayout(appCtx))
    ) || super.onSupportNavigateUp()

    override fun changeTitle(titleRes: Int) {
        val supportActionBar = supportActionBar
        supportActionBar?.setTitle(titleRes)
    }
}
