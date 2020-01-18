package labone.counters

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.synthetic.main.fragment_my_ticket.*

class MyTicketFragment : BaseMvRxFragment(R.layout.fragment_my_ticket) {

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)

    override fun invalidate() = withState(viewModel) { state ->
        preference.withModels {
            state.performances.forEach { performance ->
                if (performance.buy) {
                    performanceView {
                        id(performance.id)
                        performance(performance)
                        clickListener { _ ->
                            navigate(R.id.detailCounters, NewsDetailArgs(performance.id))
                        }
                        onCheckedChanged { completed ->
                            performance.id?.let { viewModel.setComplete(it, completed) }
                        }
                    }
                }
            }
        }
    }

    private fun navigate(@IdRes id: Int, args: Parcelable? = null) {
        findNavController().navigate(id, Bundle().apply { putParcelable(MvRx.KEY_ARG, args) })
    }
}