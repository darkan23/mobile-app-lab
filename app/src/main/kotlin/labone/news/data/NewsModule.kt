package labone.news.data

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.ViewModelKey
import labone.news.ui.NewsViewModel

@InstallIn(SingletonComponent::class)
@Module
object NewsModule {

    @Provides
    @Reusable
    internal fun provideProfileServer(newsDao: NewsDao): NewsRepository = NewsRepositoryImpl(newsDao)

    @Provides
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    fun viewModelFactory(actual: NewsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
