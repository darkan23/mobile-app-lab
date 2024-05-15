package ru.labone.addchat.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.labone.addchat.ui.AddChatViewModel
import ru.labone.addnote.ui.AddNewsViewModel
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.ViewModelKey

@InstallIn(SingletonComponent::class)
@Module
object AddChatModule {

    @Provides
    @IntoMap
    @ViewModelKey(AddChatViewModel::class)
    fun viewModelFactory(actual: AddChatViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
