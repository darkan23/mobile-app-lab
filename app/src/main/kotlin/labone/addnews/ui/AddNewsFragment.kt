package labone.addnews.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentAddNewsBinding
import labone.centerDialog
import labone.doAfterTextChanged
import labone.navigateBack
import labone.onClickWithDebounce
import labone.showToast
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
        binding.done.setColorFilter(
            color(if (state.text.isNullOrBlank()) R.color.gray_170 else R.color.white), PorterDuff.Mode.SRC_IN
        )
        binding.done.isEnabled = !state.text.isNullOrBlank()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.close.onClickWithDebounce { navigateBack() }
        binding.record.doAfterTextChanged {
            viewModel.changeText(it?.toString())
        }
        binding.done.onClickWithDebounce {
            viewModel.saveNewNews()
        }

        viewModel.effects.collect(lifecycleScope) { effects ->
            when (effects) {
                NavigationBack -> navigateBack()
            }
        }
    }
}
