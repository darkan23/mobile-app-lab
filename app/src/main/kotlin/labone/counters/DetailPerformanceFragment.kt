package labone.counters

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_detail_counter.*
import labone.formatDateTime

@SuppressLint("ParcelCreator")
@Parcelize
data class NewsDetailArgs(val id: Long?) : Parcelable

class DetailPerformance : Fragment(R.layout.fragment_detail_counter), MavericksView {

    private val args: NewsDetailArgs by args()

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)

    override fun invalidate() = withState(viewModel) { state ->
        val counterDetails = state.performances.firstOrNull { it.id == args.id }
        if (counterDetails != null) {
            counterDate.text = formatDateTime(counterDetails.date)
            countersName.text = counterDetails.performanceName
            countersNumber.text = counterDetails.performancePlace.toString()
            price.text = counterDetails.price.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editCounter.setOnClickListener {
            navigate(R.id.addCounter, AddEditTaskArgs(args.id))
        }
    }

    private fun navigate(@IdRes id: Int, args: Parcelable? = null) {
        findNavController().navigate(id, Bundle().apply { putParcelable(Mavericks.KEY_ARG, args) })
    }
}
