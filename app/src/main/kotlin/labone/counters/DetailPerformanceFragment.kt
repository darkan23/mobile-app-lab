package labone.counters

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
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

class DetailPerformance : BaseMvRxFragment(R.layout.fragment_detail_counter) {

    private val args: NewsDetailArgs by args()

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)

    override fun invalidate() = withState(viewModel) { state ->

        editCounter.setOnClickListener {
            navigate(R.id.addCounter, AddEditTaskArgs(args.id))
        }

        val counterDetails = state.performances.firstOrNull { it.id == args.id }
        if (counterDetails != null) {
            counterDate.text = formatDateTime(counterDetails.date)
            countersName.text = counterDetails.performanceName
            countersNumber.text = counterDetails.performancePlace.toString()
            price.text = counterDetails.price.toString()
        }
    }

    private fun navigate(@IdRes id: Int, args: Parcelable? = null) {
        findNavController().navigate(id, Bundle().apply { putParcelable(MvRx.KEY_ARG, args) })
    }
}
