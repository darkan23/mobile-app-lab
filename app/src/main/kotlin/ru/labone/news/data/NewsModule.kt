package ru.labone.news.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import ru.labone.AppScope
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.ViewModelKey
import ru.labone.news.ui.NewsViewModel
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NewsModule {

    @Provides
    @Singleton
    internal fun provideProfileServer(
        appScope: AppScope,
        newsDao: NewsDao,
        okHttpClient: OkHttpClient
    ): NewsRepository = NewsRepositoryImpl(appScope, newsDao, okHttpClient)

    @Provides
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    fun viewModelFactory(actual: NewsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
