package labone.counters

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import labone.server.NetworkJobSource
import javax.inject.Singleton

@Module
object PerformanceModule {

    @Provides
    @Singleton
    internal fun provideCountersService(performanceDao: PerformanceDao): PerformanceService =
        PerformanceServiceImpl(performanceDao)

    @Provides
    @IntoSet
    fun counterNetworkJobSource(service: PerformanceService): NetworkJobSource =
        service as NetworkJobSource
}
