package labone.counters

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.ViewModelKey
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PerformanceModule {

    @Provides
    @Singleton
    internal fun provideCountersService(performanceDao: PerformanceDao): PerformanceService =
        PerformanceServiceImpl(performanceDao)


    @Provides
    @IntoMap
    @ViewModelKey(PerformanceViewModel::class)
    fun viewModelFactory(actual: PerformanceViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
