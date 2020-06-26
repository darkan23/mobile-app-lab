package labone.news

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.synthetic.main.fragment_add_news.*
import labone.centerDialog
import labone.doAfterTextChanged
import labone.mvrx.BaseMvRxDialogFragment
import labone.navigateBack
import splitties.resources.color

class AddNewsFragment : BaseMvRxDialogFragment(R.layout.fragment_add_news) {

    private val viewModel by fragmentViewModel(NewsViewModel::class)

    override fun onStart() {
        dialog?.let { centerDialog(it) }
        super.onStart()
    }

    override fun invalidate() = withState(viewModel) { state ->
        if (state.text?.trim() == null) {
            done.setColorFilter(color(R.color.gray_170), PorterDuff.Mode.SRC_IN)
        } else {
            done.setColorFilter(color(R.color.white), PorterDuff.Mode.SRC_IN)
        }
        done.isEnabled = state.text?.trim() != null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        close.setOnClickListener { navigateBack() }
        record.doAfterTextChanged {
            viewModel.changeText(if (it?.toString().isNullOrBlank()) null else it.toString())
        }
        done.setOnClickListener {
            viewModel.done()
            navigateBack()
        }
    }
}
