package labone

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import mu.KotlinLogging.logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.navigationTo(navDirections: NavDirections) =
    withNavController { navigate(navDirections) }

@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.navigateBack() = withNavController { popBackStack() }

inline fun Fragment.withNavController(action: NavController.() -> Unit) {
    val currentState = lifecycle.currentState
    if (currentState.isAtLeast(Lifecycle.State.STARTED)) {
        try {
            action(findNavController())
        } catch (e: Exception) {
            logger {}.warn(e) { "Can't navigate from $this" }
        }
    } else {
        logger {}.debug { "Skip navigation from $this because of lifecycle state is $currentState" }
    }
}

fun formatDateTime(epoch: Long): String {
    val date = Date(epoch)
    val sdf = SimpleDateFormat("dd.MM HH:mm", Locale.ENGLISH)
    return sdf.format(date)
}
