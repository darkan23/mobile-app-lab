package labone.counters

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.fragment_counters.*
import labone.navigationTo

class FragmentPerformance : Fragment(R.layout.fragment_counters), MavericksView {

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)

    override fun invalidate() = withState(viewModel) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addCounter.setOnClickListener { navigationTo(FragmentPerformanceDirections.openAddNews()) }
        fl_profile_action.setOnClickListener { navigationTo(FragmentPerformanceDirections.openProfile()) }
    }
}
