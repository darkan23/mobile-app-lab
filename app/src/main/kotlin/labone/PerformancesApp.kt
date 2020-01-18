package labone

import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.crashlytics.android.Crashlytics
import com.example.labone.BuildConfig
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.fabric.sdk.android.Fabric
import labone.server.PerformancesApiModule
import java.util.Locale

class PerformancesApp : DaggerApplication() {

    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this,
            PerformancesApiModule(BuildConfig.API_BASE_URL, BuildConfig.DEBUG))
    }

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
        AndroidThreeTen.init(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        val appLifecycleObservers = component.appLifecycleObservers()
        val appLifecycle = ProcessLifecycleOwner.get().lifecycle
        for (appLifecycleObserver in appLifecycleObservers) {
            appLifecycle.addObserver(appLifecycleObserver)
        }

        adjustConfiguration(resources.configuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        adjustConfiguration(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    private fun adjustConfiguration(config: Configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(uiLocale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = uiLocale
        }
        Locale.setDefault(uiLocale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, null)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = component
}
