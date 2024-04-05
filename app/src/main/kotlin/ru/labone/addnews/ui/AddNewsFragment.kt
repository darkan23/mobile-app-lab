package ru.labone.addnews.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
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
import ru.labone.showToast
import ru.labone.viewbinding.viewBinding
import splitties.resources.colorSL

class AddNewsFragment : DialogFragment(R.layout.fragment_add_news), MavericksView {

    private val viewModel by fragmentViewModel(AddNewsViewModel::class)
    private val binding by viewBinding(FragmentAddNewsBinding::bind)
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.savePosition(position)
        }
    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2)) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.savePhoto(getFileData(uris))
            } else {
                showToast("Что-то пошло не так")
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
        binding.viewPager.registerOnPageChangeCallback(onPageChangeCallback)
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
                is RenderPhoto -> renderDocuments(effects.photoUri, effects.newPosition)
            }
        }
    }

    private fun renderDocuments(fileUri: List<Uri>, newPosition: Int) {
        val imageAdapter = ImageAdapter(fileUri)
        binding.indicator.isVisible = fileUri.size > 1
        binding.viewPager.adapter = imageAdapter
        if (fileUri.isNotEmpty()) {
            binding.viewPager.offscreenPageLimit = fileUri.size
            binding.viewPager.setCurrentItem(newPosition, false)
            binding.indicator.setViewPager(binding.viewPager)
        }
        imageAdapter.listener = {
            viewModel.delete(it)
        }
    }
}
