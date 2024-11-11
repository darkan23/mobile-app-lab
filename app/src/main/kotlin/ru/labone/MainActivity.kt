package ru.labone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
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
import ru.rustore.sdk.pushclient.RuStorePushClient
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
        askNotificationPermission()
        RuStorePushClient.getToken()
            .addOnSuccessListener { result ->
                println("FUCK $result")
            }
            .addOnFailureListener { throwable ->
                // Process error
            }
        navigatorCollect.collect(lifecycleScope, navController, this)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(
        AppBarConfiguration(navController.graph, DrawerLayout(appCtx))
    ) || super.onSupportNavigateUp()

    override fun changeTitle(titleRes: Int) {
        val supportActionBar = supportActionBar
        supportActionBar?.setTitle(titleRes)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // RuStore Push SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level>= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // RuStore Push SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
