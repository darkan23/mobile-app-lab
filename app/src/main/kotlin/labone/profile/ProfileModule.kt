package labone.profile

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.ViewModelKey

@InstallIn(SingletonComponent::class)
@Module
object ProfileModule {

    @Provides
    @Reusable
    internal fun provideProfileServer(profileDao: ProfileDao): ProfileRepository = ProfileRepositoryImpl(profileDao)

    @Provides
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun viewModelFactory(actual: ProfileViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
