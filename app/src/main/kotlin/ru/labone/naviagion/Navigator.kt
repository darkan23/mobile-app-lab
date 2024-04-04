package ru.labone.naviagion

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavController
import com.example.labone.NavGraphDirections
import ru.labone.AppScope
import ru.labone.Effects
import ru.labone.counters.AddNewsNavKey
import ru.labone.counters.NewsDetailNavKey
import mu.KotlinLogging.logger
import javax.inject.Inject

interface Navigator {

    fun navigateTo(nav: NavigationEffect)
}

interface NavigatorCollect {

    fun collect(lifecycleScope: LifecycleCoroutineScope, navController: NavController, activityContext: Context)
}

class NavigatorImpl @Inject constructor(
    val appScope: AppScope,
    val application: Application,
) : Navigator, NavigatorCollect {

    val effects: Effects<NavigationEffect> = Effects(appScope)
    private val log = logger {}

    override fun navigateTo(nav: NavigationEffect) {
        effects.publish(nav)
    }

    @Suppress("ComplexMethod", "LongMethod", "CognitiveComplexMethod")
    override fun collect(
        lifecycleScope: LifecycleCoroutineScope,
        navController: NavController,
        activityContext: Context,
    ) {
        effects.collect(lifecycleScope) { effect ->
            when (effect) {
                is NewsDetailNavKey -> navController.navigate(
                    NavGraphDirections.detailCounter(effect)
                )
                is AddNewsNavKey -> navController.navigate(
                    NavGraphDirections.addCounter()
                )
            }
        }
    }
}
