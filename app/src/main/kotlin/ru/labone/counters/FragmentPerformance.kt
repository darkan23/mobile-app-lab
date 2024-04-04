package ru.labone.counters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentCountersBinding
import ru.labone.navigationTo
import ru.labone.viewbinding.viewBinding

class FragmentPerformance : Fragment(R.layout.fragment_counters), MavericksView {

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)
    private val binding by viewBinding(FragmentCountersBinding::bind)

    override fun invalidate() = withState(viewModel) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addCounter.setOnClickListener { navigationTo(FragmentPerformanceDirections.openAddNews()) }
        binding.inclActionBar.flProfileAction.setOnClickListener {
            navigationTo(FragmentPerformanceDirections.openProfile())
        }
    }
}
