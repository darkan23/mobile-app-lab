package labone

import android.app.Application
import android.content.res.Configuration
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksViewModelConfigFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import java.util.Locale

@HiltAndroidApp
class PerformancesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Mavericks.initialize(
            applicationContext,
            viewModelConfigFactory = MavericksViewModelConfigFactory(BuildConfig.DEBUG, Dispatchers.IO)
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        adjustConfiguration(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    private fun adjustConfiguration(config: Configuration) {
        @Suppress("DEPRECATION")
        config.locale = uiLocale
        Locale.setDefault(uiLocale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, null)
    }
}
