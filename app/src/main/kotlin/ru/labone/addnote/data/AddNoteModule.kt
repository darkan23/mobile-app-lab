package ru.labone.addnote.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.labone.addnote.ui.AddNewsViewModel
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.ViewModelKey

@InstallIn(SingletonComponent::class)
@Module
object AddNoteModule {

    @Provides
    @IntoMap
    @ViewModelKey(AddNewsViewModel::class)
    fun viewModelFactory(actual: AddNewsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
