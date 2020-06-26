package labone.counters

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow

interface PerformanceService {
    fun observeActual(): Flow<List<Performance>>

    fun savePerformance(performance: Performance): Disposable

    fun saveBuy(id: Long, complete: Boolean): Disposable

    fun observeCountPerformance(): Flow<Int>

    fun sumPrice(): Flow<Int>

    fun clearCompletedTasks(): Disposable
}

@Entity
data class Performance(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val performanceName: String,
    val date: Long,
    val performancePlace: Int,
    val price: Int,
    val buy: Boolean
)

internal class PerformanceServiceImpl(
    private val performanceDao: PerformanceDao
) : PerformanceService {

    override fun observeActual(): Flow<List<Performance>> =
        performanceDao.observeActualCounters()

    override fun savePerformance(performance: Performance): Disposable =
        fromAction { performanceDao.savePerformance(performance) }

    override fun saveBuy(id: Long, complete: Boolean): Disposable =
        fromAction { performanceDao.setComplete(id, complete) }

    override fun clearCompletedTasks(): Disposable =
        fromAction { performanceDao.clearCompletedTasks() }

    override fun observeCountPerformance(): Flow<Int> =
        performanceDao.observeUnreadCount()

    override fun sumPrice(): Flow<Int> = performanceDao.observePriceCount()


    private fun fromAction(action: () -> Unit): Disposable = Completable.fromAction(action)
        .subscribeOn(Schedulers.io())
        .subscribe()
}

interface Test {
    fun sumPrice(sum: List<Performance>): Int
}

internal class TestImpl : Test {

    override fun sumPrice(sum: List<Performance>): Int {
        var sumPrise = 0
        sum.forEach {
            sumPrise += it.price
        }
        return sumPrise / sum.size
    }
}
