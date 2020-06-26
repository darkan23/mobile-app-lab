package labone.counters

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.fragment_counters.*
import labone.navigationTo

class FragmentPerformance : BaseMvRxFragment(R.layout.fragment_counters) {

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)

    override fun invalidate() = withState(viewModel) { state ->

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addCounter.setOnClickListener { navigationTo(FragmentPerformanceDirections.openAddNews()) }
        fl_profile_action.setOnClickListener { navigationTo(FragmentPerformanceDirections.openProfile()) }
    }
}
