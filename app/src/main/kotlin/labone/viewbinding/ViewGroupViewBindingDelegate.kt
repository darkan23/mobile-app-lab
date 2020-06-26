package labone.viewbinding

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewGroupViewBindingDelegate<T : ViewBinding>(
    val viewGroup: ViewGroup,
    val viewBindingFactory: (View) -> T,
) : ReadOnlyProperty<ViewGroup, T> {

    private var binding: T? = null

    override fun getValue(thisRef: ViewGroup, property: KProperty<*>): T =
        binding ?: viewBindingFactory(thisRef.rootView).also { this.binding = it }
}

fun <T : ViewBinding> ViewGroup.viewBinding(
    viewBindingFactory: (View) -> T,
) = ViewGroupViewBindingDelegate(this, viewBindingFactory)
