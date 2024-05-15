package ru.labone.addchat.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentAddChatBinding
import ru.labone.doAfterTextChanged
import ru.labone.fullScreenDialog
import ru.labone.navigateBack
import ru.labone.viewbinding.viewBinding
import splitties.resources.colorSL

class AddChatFragment : DialogFragment(R.layout.fragment_add_chat), MavericksView {

    private val viewModel by fragmentViewModel(AddChatViewModel::class)
    private val binding by viewBinding(FragmentAddChatBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenDialog()
    }

    private val menuAction by lazy {
        binding.addNewTab.menu.getItem(0)
    }

    override fun invalidate() = withState(viewModel) { state ->
        val hasEnable = !state.nameChat.isNullOrBlank()
        menuAction.setIconTintList(
            colorSL(if (hasEnable) R.color.colorPrimary else R.color.disable_color)
        )
        menuAction.isEnabled = hasEnable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addNewTab.setNavigationOnClickListener { navigateBack() }
        binding.record.doAfterTextChanged {
            viewModel.changeNameChat(it?.toString())
        }

        menuAction.setOnMenuItemClickListener {
            viewModel.saveNewChat()
            true
        }

        viewModel.effects.collect(lifecycleScope) { effect ->
            when (effect) {
                NavigationBack -> navigateBack()
            }
        }
    }
}
