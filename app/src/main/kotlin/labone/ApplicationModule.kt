package labone

import com.astroid.yodha.mvrx.ViewModelKey
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Provides
import dagger.multibindings.IntoMap
import labone.counters.PerformanceViewModel
import labone.mvrx.AssistedViewModelFactory

private const val DEFAULT_THROTTLE_SYNC_PERIOD = 5000L

@AssistedModule
@dagger.Module(includes = [AssistedInject_ApplicationModule::class])
object ApplicationModule {

    @Provides
    @IntoMap
    @ViewModelKey(PerformanceViewModel::class)
    fun viewModelFactory(actual: PerformanceViewModel.Factory): AssistedViewModelFactory<*, *> =
        actual
}
