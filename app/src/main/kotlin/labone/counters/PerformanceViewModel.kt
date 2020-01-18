package labone.counters

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DaggerViewModelFactory
import labone.util.copy
import labone.util.upsert
import org.threeten.bp.Instant

class PerformanceViewModel @AssistedInject constructor(
    @Assisted initialState: CountersState,
    private val performanceService: PerformanceService
) : BaseMvRxViewModel<CountersState>(initialState, BuildConfig.DEBUG) {

    init {
        performanceService.observeCountPerformance().toObservable()
            .execute { sum: Async<Int> ->
                if (sum is Success) {
                    copy(
                        sum = sum()
                    )
                } else {
                    this
                }
            }

        performanceService.observeActual()
            .toObservable()
            .execute { performance: Async<List<Performance>> ->
                if (performance is Success) copy(
                    performances = performance()
                ) else this
            }
    }

    fun savePreference(name: String, place: String, price: String) = withState { state ->
        val newCounter = Performance(
            date = Instant.now().toEpochMilli(),
            performanceName = name,
            performancePlace = place.toInt(),
            price = price.toInt(),
            buy = state.buy
        )
        setState {
            copy(
                performances = sort(
                    performances.upsert(newCounter) { it.id == newCounter.id },
                    sortedBy
                )
            )

        }
        performanceService.savePerformance(newCounter)
    }

    fun setComplete(id: Long, complete: Boolean) {
        setState {
            val task = performances.firstOrNull { it.id == id } ?:return@setState this
            if (task.buy == complete) return@setState this
            copy(
                performances = performances.copy(
                    performances.indexOf(task),
                    task.copy(buy = complete)
                )
            )
        }
        performanceService.saveBuy(id, complete)
    }

    fun clearCompletedTasks() = setState {
        performanceService.clearCompletedTasks()
        copy(performances = performances.filter { !it.buy })
    }

    private fun sort(uiSale: List<Performance>, sortType: Sorted): List<Performance> =
        when (sortType) {
            Sorted.ASC_DATE -> uiSale.sortedWith(compareBy(Performance::date))
            Sorted.DESC_DATE -> uiSale.sortedWith(compareByDescending(Performance::date))
        }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<PerformanceViewModel, CountersState>

    companion object : DaggerViewModelFactory<PerformanceViewModel, CountersState>(
        PerformanceViewModel::class
    )
}

data class CountersState(
    val performances: List<Performance> = emptyList(),
    val sum: Int = 0,
    val sumPrice: Int = 0,
    val buy: Boolean = true,
    val sortedBy: Sorted = Sorted.DESC_DATE
) : MvRxState

enum class Sorted {
    ASC_DATE,
    DESC_DATE
}
