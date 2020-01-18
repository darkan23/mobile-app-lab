package labone.mvrx

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import labone.PerformancesApp
import kotlin.reflect.KClass

abstract class DaggerViewModelFactory<VM : BaseMvRxViewModel<S>, S : MvRxState>(
    private val viewModelClass: KClass<out VM>
) : MvRxViewModelFactory<VM, S> {

    override fun create(viewModelContext: ViewModelContext, state: S): VM? {
        val viewModelFactoryMap =
            viewModelContext.app<PerformancesApp>().component.mvrxViewModelFactories()
        @Suppress("UNCHECKED_CAST")
        val factory = viewModelFactoryMap[viewModelClass.java] as? AssistedViewModelFactory<VM, S>
        requireNotNull(factory) { "ViewFactory not provided for key $viewModelClass" }
        return factory.create(state)
    }
}
