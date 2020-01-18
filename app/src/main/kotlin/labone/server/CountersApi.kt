@file:ContextualSerialization(
    Locale::class,
    LocalDateTime::class,
    LocalDate::class,
    LocalTime::class,
    Instant::class
)

package labone.server

import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.Locale

/**
 * Remember all messages ids is a string
 */
interface CountersApi {

    fun counter(): Single<Performance>

    fun payment(payment: Payment): Completable


    sealed class ExchangeStatus {
        object NotActive : ExchangeStatus()
        data class InProgress(val title: String) : ExchangeStatus()
        data class Error(
            val error: String,
            val hasInternetConnection: Boolean,
            val exception: Exception? = null
        ) : ExchangeStatus()
    }
}

@Serializable
data class Performance(
    val number: Int,
    val name: String,
    val id: Long
)

@Serializable
data class Payment(
    val id: Long,
    val idCounter: Long,
    val counterReading: Long,
    val lastUpdate: LocalDate
)

@Serializable
data class Changes(
    val continuationToken: String,
    val performance: List<Performance>,
    val payment: List<Payment>
)

interface Prioritized {
    val priority: Short
        get() = 0
}

interface ChangesReceiver : Prioritized {
    fun consume(changes: Changes)
}

interface ApiRepository {

    fun loadContinuationToken(): String?

    fun saveContinuationToken(token: String?)
}
