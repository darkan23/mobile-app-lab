package ru.labone.addnews.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.labone.addnews.ui.AddNewsViewModel
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.ViewModelKey

@InstallIn(SingletonComponent::class)
@Module
object AddNewsModule {

    @Provides
    @IntoMap
    @ViewModelKey(AddNewsViewModel::class)
    fun viewModelFactory(actual: AddNewsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
