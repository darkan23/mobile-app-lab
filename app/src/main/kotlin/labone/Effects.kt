package labone

import androidx.lifecycle.LifecycleCoroutineScope
import com.airbnb.mvrx.MavericksViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging

class Effects<E : Any>(private val publicationScope: CoroutineScope) {
    private val effectsChannel: Channel<E> = Channel()
    private val effectsFlow = effectsChannel.receiveAsFlow()
    private val log = KotlinLogging.logger {}

    fun publish(effect: E) {
        log.debug { "Publishing effect: $effect" }
        publicationScope.launch { effectsChannel.send(effect) }
    }

    suspend fun publish(builder: suspend () -> E) {
        publish(builder())
    }

    @Suppress("TooGenericExceptionCaught")
    fun collect(collectionScope: LifecycleCoroutineScope, processor: suspend (E) -> Unit) {
        collectionScope.launchWhenStarted {
            effectsFlow.collect { effect ->
                log.debug { "Processing effect: $effect" }
                try {
                    processor(effect)
                    log.debug { "Effect processed: $effect" }
                } catch (e: Exception) {
                    log.error(e) { "Effect processing fail: $effect" }
                }
            }
        }
    }
}

fun <E : Any> MavericksViewModel<*>.effects(): Effects<E> = Effects<E>(viewModelScope)
