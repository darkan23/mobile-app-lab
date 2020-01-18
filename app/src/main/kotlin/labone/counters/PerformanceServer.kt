package labone.counters

import androidx.room.Entity
import androidx.room.PrimaryKey
import dagger.Module
import dagger.Provides
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

interface PerformanceService {
    fun observeActual(): Flowable<List<Performance>>

    fun savePerformance(performance: Performance): Disposable

    fun saveBuy(id: Long, complete: Boolean): Disposable

    fun observeCountPerformance(): Flowable<Int>

    fun sumPrice(): Flowable<Int>

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

    override fun observeActual(): Flowable<List<Performance>> =
        performanceDao.observeActualCounters()

    override fun savePerformance(performance: Performance): Disposable =
        fromAction { performanceDao.savePerformance(performance) }

    override fun saveBuy(id: Long, complete: Boolean): Disposable =
        fromAction { performanceDao.setComplete(id, complete) }

    override fun clearCompletedTasks(): Disposable =
        fromAction { performanceDao.clearCompletedTasks() }

    override fun observeCountPerformance(): Flowable<Int> =
        performanceDao.observeUnreadCount()

    override fun sumPrice(): Flowable<Int> = performanceDao.observePriceCount()


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

@Module
object TestModule {

    @Provides
    @Singleton
    internal fun provideCountersService(): Test = TestImpl()
}

