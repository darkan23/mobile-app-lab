package labone.counters

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.synthetic.main.fragment_counters.*
import kotlinx.android.synthetic.main.item_counters.view.*
import labone.formatDateTime
import labone.navigationTo
import splitties.resources.color
import splitties.resources.str

class FragmentPerformance : BaseMvRxFragment(R.layout.fragment_counters) {

    private val viewModel by fragmentViewModel(PerformanceViewModel::class)

    override fun invalidate() = withState(viewModel) { state ->
        sum.text = state.sum.toString()
        if (state.performances.isEmpty()) {
            sumPrice.text = "0"
        } else {
            var sumPrise = 0
            state.performances.forEach { sumPrise += it.price }
            sumPrice.text = (sumPrise / state.performances.size).toString()
        }
        counters.withModels {
            state.performances.forEach { counters ->
                performanceView {
                    id(counters.id)
                    performance(counters)
                    clickListener { _ ->
                        R.id.detailCounters.navigate(NewsDetailArgs(counters.id))
                    }
                    onCheckedChanged { completed ->
                        counters.id?.let {
                            viewModel.setComplete(
                                it,
                                completed
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addCounter.setOnClickListener { navigationTo(FragmentPerformanceDirections.addCounter()) }
    }

    private fun Int.navigate(args: Parcelable? = null) {
        findNavController().navigate(this, Bundle().apply { putParcelable(MvRx.KEY_ARG, args) })
    }
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class PerformanceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.item_counters, this)
    }

    @ModelProp
    fun setPerformance(performance: Performance) {
        counterDate.text = formatDateTime(performance.date)
        countersName.text = performance.performanceName
        countersNumber.text = performance.performancePlace.toString()
        price.text = performance.price.toString()
        buy.text = if (performance.buy) str(R.string.cancel) else str(R.string.buy)
        buy.setBackgroundColor(if (performance.buy) color(R.color.ad_color) else color(R.color.green))
    }

    @CallbackProp
    fun clickListener(listener: OnClickListener?) {
        setOnClickListener(listener)
    }


    @CallbackProp
    fun onCheckedChanged(listener: ((Boolean) -> Unit)?) {
        if (listener == null) {
            buy.setOnCheckedChangeListener(null)
        } else {
            buy.setOnCheckedChangeListener { _, isChecked -> listener(isChecked) }
        }
    }
}
