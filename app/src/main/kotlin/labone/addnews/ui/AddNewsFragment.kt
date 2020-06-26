package labone.addnews.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentAddNewsBinding
import labone.centerDialog
import labone.doAfterTextChanged
import labone.navigateBack
import labone.viewbinding.viewBinding
import splitties.resources.color

class AddNewsFragment : DialogFragment(R.layout.fragment_add_news), MavericksView {

    private val viewModel by fragmentViewModel(AddNewsViewModel::class)
    private val binding by viewBinding(FragmentAddNewsBinding::bind)

    override fun onStart() {
        dialog?.let { centerDialog(it) }
        super.onStart()
    }

    override fun invalidate() = withState(viewModel) { state ->
        if (state.text?.trim() == null) {
            binding.done.setColorFilter(color(R.color.gray_170), PorterDuff.Mode.SRC_IN)
        } else {
            binding.done.setColorFilter(color(R.color.white), PorterDuff.Mode.SRC_IN)
        }
        binding.done.isEnabled = state.text?.trim() != null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.close.setOnClickListener { navigateBack() }
        binding.record.doAfterTextChanged {
            viewModel.changeText(if (it?.toString().isNullOrBlank()) null else it.toString())
        }
        binding.done.setOnClickListener {
            viewModel.saveNewNews()
            navigateBack()
        }
    }
}
