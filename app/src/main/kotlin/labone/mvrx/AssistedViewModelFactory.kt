package labone.mvrx

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState

interface AssistedViewModelFactory<VM : BaseMvRxViewModel<S>, S : MvRxState> {
    fun create(initialState: S): VM
}
