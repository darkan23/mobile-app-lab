package labone.news.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import labone.AppScope
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.ViewModelKey
import labone.news.ui.NewsViewModel
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NewsModule {

    @Provides
    @Singleton
    internal fun provideProfileServer(
        appScope: AppScope,
        newsDao: NewsDao,
    ): NewsRepository = NewsRepositoryImpl(appScope, newsDao)

    @Provides
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    fun viewModelFactory(actual: NewsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
