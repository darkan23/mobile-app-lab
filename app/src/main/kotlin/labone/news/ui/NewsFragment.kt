package labone.news.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentNewsBinding
import com.example.labone.databinding.ItemNewsBinding
import labone.formatDateTime
import labone.news.data.News
import labone.viewbinding.viewBinding

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
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class NewsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by viewBinding(ItemNewsBinding::bind)

    init {
        inflate(context, R.layout.item_news, this)
    }

    @ModelProp
    fun setNews(performance: News) {
        binding.speakerName.text = formatDateTime(performance.date)
        binding.newsBody.text = performance.text
    }
}
