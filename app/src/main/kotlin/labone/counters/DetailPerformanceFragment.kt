package labone.counters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentDetailCounterBinding
import labone.formatDateTime
import labone.viewbinding.viewBinding

class DetailPerformance : Fragment(R.layout.fragment_detail_counter), MavericksView {

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)
    private val binding by viewBinding(FragmentDetailCounterBinding::bind)
    private val args: DetailPerformanceArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (args.newsDetailNavKey != null) {
            arguments = Bundle().apply {
                putParcelable(Mavericks.KEY_ARG, args.newsDetailNavKey)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun invalidate() = withState(viewModel) { state ->
        val counterDetails = state.performances.firstOrNull { it.id == state.id }
        if (counterDetails != null) {
            binding.counterDate.text = formatDateTime(counterDetails.date)
            binding.countersName.text = counterDetails.performanceName
            binding.countersNumber.text = counterDetails.performancePlace.toString()
            binding.price.text = counterDetails.price.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editCounter.setOnClickListener {
        }
    }
}
