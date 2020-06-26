package labone.addnews.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import labone.addnews.ui.AddNewsViewModel
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.ViewModelKey

@InstallIn(SingletonComponent::class)
@Module
object AddNewsModule {

    @Provides
    @IntoMap
    @ViewModelKey(AddNewsViewModel::class)
    fun viewModelFactory(actual: AddNewsViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
