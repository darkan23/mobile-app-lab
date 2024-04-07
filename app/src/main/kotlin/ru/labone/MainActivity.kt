package ru.labone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.labone.R
import com.example.labone.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.labone.naviagion.NavigatorCollect
import ru.labone.viewbinding.viewBinding
import splitties.init.appCtx
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActionBarTitleChanger {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    @Inject
    lateinit var navigatorCollect: NavigatorCollect

    private val navController: NavController
        get() = findNavController(R.id.navHostFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.tbMain)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        NavigationUI.setupWithNavController(binding.bnvNav, navController)
        navigatorCollect.collect(lifecycleScope, navController, this)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(
        AppBarConfiguration(navController.graph, DrawerLayout(appCtx))
    ) || super.onSupportNavigateUp()

    override fun changeTitle(titleRes: Int) {
        val supportActionBar = supportActionBar
        supportActionBar?.setTitle(titleRes)
    }
}
