package labone.counters

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import labone.AppScope

interface PerformanceService {
    fun flowActual(): Flow<List<Performance>>

    fun flowCountPerformance(): Flow<Int>

    fun flowSumPrice(): Flow<Int>

    fun savePerformance(performance: Performance)

    fun saveBuy(id: Long, complete: Boolean)

    fun clearCompletedTasks()
}

@Entity
data class Performance(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val performanceName: String,
    val date: Long,
    val performancePlace: Int,
    val price: Int,
    val buy: Boolean,
)

internal class PerformanceServiceImpl(
    private val performanceDao: PerformanceDao,
    private val appScope: AppScope,
) : PerformanceService {

    override fun flowCountPerformance(): Flow<Int> = performanceDao.flowUnreadCount()

    override fun flowActual(): Flow<List<Performance>> = performanceDao.flowActualCounters()

    override fun flowSumPrice(): Flow<Int> = performanceDao.flowPriceCount()

    override fun savePerformance(performance: Performance) {
        appScope.launch(Dispatchers.IO) {
            performanceDao.savePerformance(performance)
        }
    }

    override fun saveBuy(id: Long, complete: Boolean) {
        appScope.launch(Dispatchers.IO) {
            performanceDao.setComplete(id, complete)
        }
    }

    override fun clearCompletedTasks() {
        appScope.launch(Dispatchers.IO) {
            performanceDao.clearCompletedTasks()
        }
    }
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
