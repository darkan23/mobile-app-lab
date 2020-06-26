package labone.news

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.item_news.view.*
import labone.formatDateTime

class NewsFragment : BaseMvRxFragment(R.layout.fragment_news) {

    private val viewModel by fragmentViewModel(NewsViewModel::class)

    override fun invalidate() = withState(viewModel) { state ->

        news.withModels {
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

    init {
        inflate(context, R.layout.item_news, this)
    }

    @ModelProp
    fun setNews(performance: News) {
        speakerName.text = formatDateTime(performance.date)
        newsBody.text = performance.text
    }
}
