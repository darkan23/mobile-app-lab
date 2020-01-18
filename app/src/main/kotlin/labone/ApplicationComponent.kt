package labone

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import com.airbnb.mvrx.BaseMvRxViewModel
import labone.mvrx.AssistedViewModelFactory
import dagger.BindsInstance
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import labone.counters.PerformanceModule
import labone.counters.TestModule
import labone.dataBase.RoomModule
import labone.server.PerformancesApiModule
import labone.server.NetworkJobModule
import okhttp3.OkHttpClient
import javax.inject.Singleton

@dagger.Component(
    modules = [
        AndroidInjectionModule::class,
        PerformanceModule::class,
        TestModule::class,
        PerformancesApiModule::class,
        NetworkJobModule::class,
        ApplicationModule::class,
        RoomModule::class]
)

@Singleton
interface ApplicationComponent : AndroidInjector<PerformancesApp> {
    fun appLifecycleObservers(): Set<LifecycleObserver>

    fun mvrxViewModelFactories(): Map<Class<out BaseMvRxViewModel<*>>, AssistedViewModelFactory<*, *>>

    fun okHttpClient(): OkHttpClient

    @dagger.Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application, performancesApiModule: PerformancesApiModule): ApplicationComponent
    }
}
