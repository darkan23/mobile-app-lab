package ru.labone.viewbinding

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ActivityViewBinding<T : ViewBinding>(
    activity: AppCompatActivity,
    private val viewBindingFactory: (LayoutInflater) -> T,
) : ReadOnlyProperty<AppCompatActivity, T> {

    private var binding: T? = null

    init {
        activity.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            }
        )
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T =
        binding ?: viewBindingFactory(thisRef.layoutInflater).also { binding = it }
}

fun <T : ViewBinding> AppCompatActivity.viewBinding(
    viewBindingFactory: (LayoutInflater) -> T,
) = ActivityViewBinding(this, viewBindingFactory)
