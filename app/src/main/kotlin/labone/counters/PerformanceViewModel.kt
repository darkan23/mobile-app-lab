package labone.counters

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DiMavericksViewModelFactory
import labone.naviagion.Navigator
import labone.util.copy
import java.time.Instant

class PerformanceViewModel @AssistedInject constructor(
    @Assisted initialState: CountersState,
    private val performanceService: PerformanceService,
    private val navigator: Navigator,
) : MavericksViewModel<CountersState>(initialState) {

    init {
        performanceService.flowCountPerformance()
            .execute { sum: Async<Int> ->
                if (sum is Success) {
                    copy(sum = sum())
                } else {
                    this
                }
            }

        performanceService.flowActual()
            .execute { performance: Async<List<Performance>> ->
                if (performance is Success) {
                    copy(performances = sort(performance(), sortedBy))
                } else {
                    this
                }
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
        performanceService.savePerformance(newCounter)
    }

    fun setComplete(id: Long, complete: Boolean) {
        setState {
            val task = performances.firstOrNull { it.id == id } ?: return@setState this
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

    fun navigateToAddNews() = withState { state ->
        navigator.navigateTo(
            NewsDetailNavKey(
                state.id
            )
        )
    }

    private fun sort(uiSale: List<Performance>, sortType: Sorted): List<Performance> = when (sortType) {
        Sorted.ASC_DATE -> uiSale.sortedWith(compareBy(Performance::date))
        Sorted.DESC_DATE -> uiSale.sortedWith(compareByDescending(Performance::date))
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<PerformanceViewModel, CountersState> {
        override fun create(initialState: CountersState): PerformanceViewModel
    }

    companion object :
        DiMavericksViewModelFactory<PerformanceViewModel, CountersState>(PerformanceViewModel::class.java)
}

data class CountersState(
    val id: Long = 0,
    val performances: List<Performance> = emptyList(),
    val sum: Int = 0,
    val sumPrice: Int = 0,
    val buy: Boolean = true,
    val sortedBy: Sorted = Sorted.DESC_DATE,
) : MavericksState {
    constructor(arg: NewsDetailNavKey) : this(
        id = arg.id
    )
}

enum class Sorted {
    ASC_DATE,
    DESC_DATE
}
