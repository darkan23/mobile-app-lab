package labone.profile

import com.astroid.yodha.mvrx.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoMap
import labone.mvrx.AssistedViewModelFactory

@Module
object ProfileModule {

    @Provides
    @Reusable
    internal fun provideProfileServer(profileDao: ProfileDao): ProfileService = ProfileServiceImpl(profileDao)

    @Provides
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun viewModelFactory(actual: ProfileViewModel.Factory): AssistedViewModelFactory<*, *> = actual
}
