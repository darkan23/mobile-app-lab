package ru.labone.chat.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.labone.chat.ui.ChatViewModel
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.ViewModelKey

@InstallIn(SingletonComponent::class)
@Module
object ChatModule {

    @Provides
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    fun viewModelFactory(actual: ChatViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
