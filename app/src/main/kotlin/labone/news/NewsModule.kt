package labone.news

import com.astroid.yodha.mvrx.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoMap
import labone.mvrx.AssistedViewModelFactory

@Module
object NewsModule {

    @Provides
    @Reusable
    internal fun provideProfileServer(newsDao: NewsDao): NewsService = NewsServiceImpl(newsDao)

    @Provides
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    fun viewModelFactory(actual: NewsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
