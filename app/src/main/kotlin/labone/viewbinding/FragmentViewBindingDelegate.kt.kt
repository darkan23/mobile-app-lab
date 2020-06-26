package labone.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T,
) : ReadOnlyProperty<Fragment, T> {

    var binding: T? = null

    init {
        val viewLifecycleOwnerLiveData = fragment.viewLifecycleOwnerLiveData
        viewLifecycleOwnerLiveData.observe(fragment) { lifecycleOwner ->
            lifecycleOwner.lifecycle.addObserver(
                object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        binding = null
                    }
                }
            )
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
        binding ?: viewBindingFactory(thisRef.requireView()).also { binding = it }
}

fun <T : ViewBinding> Fragment.viewBinding(
    viewBindingFactory: (View) -> T,
) = FragmentViewBindingDelegate(this, viewBindingFactory)
