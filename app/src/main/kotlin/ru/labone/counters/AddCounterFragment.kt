package ru.labone.counters

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentAddPaymentBinding
import ru.labone.fullScreenDialog
import ru.labone.navigateBack
import ru.labone.viewbinding.viewBinding

class AddCounterFragment : DialogFragment(R.layout.fragment_add_payment), MavericksView {
    private val viewModel by fragmentViewModel(PerformanceViewModel::class)
    private val binding by viewBinding(FragmentAddPaymentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenDialog()
    }

    override fun invalidate() = withState(viewModel) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.saveCounter.setOnClickListener {
            val name = binding.addNameCounter.text
            val number = binding.addNumberCounter.text
            val price = binding.addPriceCounter.text
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
