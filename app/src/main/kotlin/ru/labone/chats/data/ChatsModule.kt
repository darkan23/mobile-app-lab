package ru.labone.chats.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.labone.AppScope
import ru.labone.chats.ui.ChatsViewModel
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.ViewModelKey
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ChatsModule {

    @Provides
    @Singleton
    internal fun provideChatsRepo(
        appScope: AppScope,
        chatDao: ChatDao,
    ): ChatsRepository = ChatsRepositoryImpl(appScope, chatDao)

    @Provides
    @IntoMap
    @ViewModelKey(ChatsViewModel::class)
    fun viewModelFactory(actual: ChatsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
