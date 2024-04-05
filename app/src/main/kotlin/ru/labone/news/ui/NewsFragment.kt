package ru.labone.news.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentNewsBinding
import com.example.labone.databinding.ItemDocumentBinding
import com.example.labone.databinding.ItemNewsBinding
import ru.labone.ActionBarTitleChanger
import ru.labone.DocumentType.AUDIO
import ru.labone.DocumentType.DOCUMENT
import ru.labone.DocumentType.PICTURE
import ru.labone.convertTimeTo
import ru.labone.formatFileSize
import ru.labone.news.data.Document
import ru.labone.news.data.News
import ru.labone.viewbinding.viewBinding
import java.time.Instant

class NewsFragment : Fragment(R.layout.fragment_news), MavericksView {

    private val viewModel by fragmentViewModel(NewsViewModel::class)
    private val binding by viewBinding(FragmentNewsBinding::bind)

    override fun invalidate() = withState(viewModel) { state ->
        binding.news.withModels {
            state.news.forEach { news ->
                newsView {
                    id(news.id)
                    news(news)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is ActionBarTitleChanger) {
            val actionBarTitleChanger = activity as? ActionBarTitleChanger
            actionBarTitleChanger?.changeTitle(R.string.news)
        }
    }
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class NewsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by viewBinding(ItemNewsBinding::bind)

    private var files: List<Document> = emptyList()

    init {
        inflate(context, R.layout.item_news, this)
    }

    @ModelProp
    fun setNews(performance: News) {
        binding.speakerName.text = Instant.ofEpochMilli(performance.date).convertTimeTo()
        files = performance.document.documents
        binding.newsBody.text = performance.text
    }

    @AfterPropsSet
    fun postBindSetup() {
        val images = files.filter { it.type == PICTURE }
        val documents = files.filter { it.type == DOCUMENT }
        val audios = files.filter { it.type == AUDIO }
        renderImages(images)
        renderDocument(documents)
    }

    private fun renderImages(files: List<Document>) {
        binding.indicator.isVisible = files.size > 1
        binding.viewPager.isVisible = files.isNotEmpty()
        if (files.isNotEmpty()) {
            val imageAdapter = ImageViewNewsAdapter(files.map { it.path })
            binding.viewPager.adapter = imageAdapter
            binding.viewPager.offscreenPageLimit = files.size
            binding.indicator.setViewPager(binding.viewPager)
        }
    }

    private fun renderDocument(fileData: List<Document>) {
        binding.variants.isVisible = files.isNotEmpty()
        binding.variants.removeAllViews()
        fileData.forEach { file ->
            val bindingVariant = ItemDocumentBinding.inflate(LayoutInflater.from(context), this, false)
            bindingVariant.text.text = file.name
            bindingVariant.size.text = formatFileSize(file.size)
            bindingVariant.remove.isVisible = false
            binding.variants.addView(bindingVariant.root)
        }
    }
}
