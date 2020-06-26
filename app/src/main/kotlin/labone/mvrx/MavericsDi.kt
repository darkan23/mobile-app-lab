package labone.mvrx

import androidx.activity.ComponentActivity
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import dagger.MapKey
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

interface AssistedViewModelFactory<VM : MavericksViewModel<S>, S : MavericksState> {
    fun create(initialState: S): VM
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class ViewModelKey(val value: KClass<out MavericksViewModel<*>>)

abstract class DiMavericksViewModelFactory<VM : MavericksViewModel<S>, S : MavericksState>(
    private val viewModelClass: Class<out MavericksViewModel<S>>,
) : MavericksViewModelFactory<VM, S> {

    override fun create(viewModelContext: ViewModelContext, state: S): VM? =
        createViewModel(viewModelContext.activity, state)

    private fun <VM : MavericksViewModel<S>, S : MavericksState> createViewModel(
        fragmentActivity: ComponentActivity,
        state: S,
    ): VM? {
        val viewModelFactoryMap = EntryPoints.get(
            fragmentActivity.application, HiltMavericksViewModelFactoryEntryPoint::class.java
        ).viewModelFactories
        val viewModelFactory = viewModelFactoryMap[viewModelClass]

        @Suppress("UNCHECKED_CAST")
        val castedViewModelFactory = viewModelFactory as? AssistedViewModelFactory<VM, S>
        val viewModel = castedViewModelFactory?.create(state)
        return viewModel as? VM
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltMavericksViewModelFactoryEntryPoint {
    val viewModelFactories: Map<Class<out MavericksViewModel<*>>, AssistedViewModelFactory<*, *>>
}
