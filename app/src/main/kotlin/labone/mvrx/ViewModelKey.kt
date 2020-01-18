package com.astroid.yodha.mvrx

import com.airbnb.mvrx.BaseMvRxViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MapKey
annotation class ViewModelKey(val value: KClass<out BaseMvRxViewModel<*>>)
