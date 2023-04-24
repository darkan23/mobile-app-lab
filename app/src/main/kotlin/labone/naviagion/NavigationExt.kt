package labone.naviagion

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import mu.KotlinLogging.logger

@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.navigationTo(navDirections: NavDirections) =
    withNavController {
        val destinationId = graph.getAction(navDirections.actionId)?.destinationId
        if (currentDestination?.id != destinationId) {
            navigate(navDirections)
        }
    }

@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.navigateBack() = withNavController { popBackStack() }

inline fun LifecycleOwner.withNavController(
    navControllerAccessor: () -> NavController,
    action: NavController.() -> Unit,
) {
    val currentLifeCycleState = lifecycle.currentState
    if (currentLifeCycleState.isAtLeast(Lifecycle.State.STARTED)) {
        try {
            action(navControllerAccessor())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger {}.warn(e) { "Can't navigate from $this" }
        }
    } else {
        logger {}.debug { "Skip navigation from $this because of lifecycle state is $currentLifeCycleState" }
    }
}

inline fun Fragment.withNavController(action: NavController.() -> Unit) {
    val currentState = lifecycle.currentState
    if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
        try {
            action(findNavController())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger {}.warn(e) { "Can't navigate from $this" }
        }
    } else {
        logger {}.debug { "Skip navigation from $this because of lifecycle state is $currentState" }
    }
}

fun <T : Any> Fragment.navigateBackWithArgs(key: String, data: T, doBack: Boolean = true) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
    if (doBack) {
        navigateBack()
    }
}

fun <T : Any> Fragment.getBackStackData(key: String, result: (T) -> (Unit)) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.observe(viewLifecycleOwner) {
        result(it)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun NavController.navigateToRoot() = popBackStack(graph.startDestDisplayName, false)

fun NavController.getDestinationIdFromAction(actionId: Int): Int? =
    currentDestination?.getAction(actionId)?.destinationId

object NavigationExtForJava {

    @JvmStatic
    fun navigateBack(fragment: Fragment) = fragment.navigateBack()
}
