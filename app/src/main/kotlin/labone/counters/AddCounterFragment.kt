package labone.counters

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_add_payment.*
import labone.navigateBack

@SuppressLint("ParcelCreator")
@Parcelize
data class AddEditTaskArgs(val id: Long?) : Parcelable

class AddCounterFragment : Fragment(R.layout.fragment_add_payment), MavericksView {
    private val viewModel by fragmentViewModel(PerformanceViewModel::class)

    override fun invalidate() = withState(viewModel) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saveCounter.setOnClickListener {
            val name = addNameCounter.text
            val number = addNumberCounter.text
            val price = addPriceCounter.text
            if (name.isNullOrBlank() || number.isNullOrBlank() || price.isNullOrBlank()) {
                Toast.makeText(
                    context,
                    "Не все поля заполнены",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.savePreference(name.toString(), number.toString(), price.toString())
                navigateBack()
            }
        }
    }
}
