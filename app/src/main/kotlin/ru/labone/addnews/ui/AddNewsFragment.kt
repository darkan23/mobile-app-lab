package ru.labone.addnews.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentAddNewsBinding
import ru.labone.doAfterTextChanged
import ru.labone.fullScreenDialog
import ru.labone.getFileData
import ru.labone.navigateBack
import ru.labone.onClickWithDebounce
import ru.labone.viewbinding.viewBinding
import splitties.resources.colorSL

class AddNewsFragment : DialogFragment(R.layout.fragment_add_news), MavericksView {

    private val viewModel by fragmentViewModel(AddNewsViewModel::class)
    private val binding by viewBinding(FragmentAddNewsBinding::bind)

    val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2)) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.savePhoto(getFileData(uris))
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenDialog()
    }

    private val menuAction by lazy {
        binding.addNewTab.menu.getItem(0)
    }

    override fun invalidate() = withState(viewModel) { state ->
        menuAction.setIconTintList(
            colorSL(if (state.text.isNullOrBlank()) R.color.gray_192 else R.color.white)
        )
        menuAction.isEnabled = !state.text.isNullOrBlank()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addNewTab.setNavigationOnClickListener { navigateBack() }
        binding.actionStartCall.onClickWithDebounce {
            pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
        binding.record.doAfterTextChanged {
            viewModel.changeText(it?.toString())
        }

        menuAction.setOnMenuItemClickListener {
            viewModel.saveNewNews()
            true
        }

        viewModel.effects.collect(lifecycleScope) { effects ->
            when (effects) {
                NavigationBack -> navigateBack()
                is RenderPhoto -> renderPhoto(effects.photoUri, effects.newPosition)
            }
        }
    }

    private fun renderPhoto(avaUri: List<Uri>, newPisition: Int) {
        val imageAdapter = ImageAdapter(avaUri)
        binding.viewPager.adapter = imageAdapter
        binding.viewPager.offscreenPageLimit = avaUri.size
        binding.viewPager.setCurrentItem(newPisition, false)
        binding.indicator.setViewPager(binding.viewPager)
        imageAdapter.listener = {
            viewModel.delete(it)
        }
    }
}
