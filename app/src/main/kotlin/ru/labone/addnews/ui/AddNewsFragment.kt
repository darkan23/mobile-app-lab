package ru.labone.addnews.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentAddNewsBinding
import com.example.labone.databinding.ItemDocumentBinding
import ru.labone.FileData
import ru.labone.DocumentType.AUDIO
import ru.labone.DocumentType.DOCUMENT
import ru.labone.DocumentType.PICTURE
import ru.labone.doAfterTextChanged
import ru.labone.formatFileSize
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

    private val pickMultiplePhoto =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2)) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.savePhoto(getFileData(uris))
            } else {
                showToast("Что-то пошло не так")
            }
        }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
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
            viewModel.pickDocument()
        }
        binding.record.doAfterTextChanged {
            viewModel.changeText(it?.toString())
        }

        menuAction.setOnMenuItemClickListener {
            viewModel.saveNewNews()
            true
        }

        viewModel.effects.collect(lifecycleScope) { effect ->
            when (effect) {
                NavigationBack -> navigateBack()
                is RenderDocument -> {
                    val images = effect.photoUri.filter { it.type == PICTURE }
                    val documents = effect.photoUri.filter { it.type == DOCUMENT }
                    val audios = effect.photoUri.filter { it.type == AUDIO }
                    renderImages(images, effect.newPosition)
                    renderDocument(documents)
                }

                is ShowToast -> showToast(effect.text)
                PickDocuments -> pickMultipleMedia.launch("*/*")
            }
        }
    }

    private fun renderImages(fileData: List<FileData>, newPosition: Int) {
        val fileUris = fileData.map { it.uri }
        val filesCount = fileUris.size
        binding.viewPager.isVisible = fileUris.isNotEmpty()
        binding.indicator.isVisible = filesCount > 1
        if (fileUris.isNotEmpty()) {
            val imageAdapter = ImageAdapter(fileData)
            binding.viewPager.adapter = imageAdapter
            binding.viewPager.offscreenPageLimit = filesCount
            binding.viewPager.setCurrentItem(newPosition, false)
            binding.indicator.setViewPager(binding.viewPager)
            imageAdapter.listener = { pos, id ->
                viewModel.deleteImage(pos, id)
            }
        }
    }

    private fun renderDocument(fileData: List<FileData>) {
        binding.variants.removeAllViews()
        fileData.forEach { file ->
            val bindingVariant = ItemDocumentBinding.inflate(LayoutInflater.from(context))
            bindingVariant.text.text = file.name
            bindingVariant.size.text = formatFileSize(file.size)
            bindingVariant.remove.setOnClickListener {
                viewModel.deleteImage(id = file.id)
            }
            binding.variants.addView(bindingVariant.root)
        }
    }
}
