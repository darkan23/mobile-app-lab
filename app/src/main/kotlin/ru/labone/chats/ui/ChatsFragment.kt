package ru.labone.chats.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentChatsBinding
import com.example.labone.databinding.ItemChatsBinding
import ru.labone.ActionBarTitleChanger
import ru.labone.chats.data.Chat
import ru.labone.convertTimeTo
import ru.labone.loadWithCircle
import ru.labone.news.ui.dividerView
import ru.labone.onClickWithDebounce
import ru.labone.viewbinding.viewBinding
import java.time.Instant

class ChatsFragment : Fragment(R.layout.fragment_chats), MavericksView {

    private val viewModel by fragmentViewModel(ChatsViewModel::class)
    private val binding by viewBinding(FragmentChatsBinding::bind)

    override fun invalidate() = withState(viewModel) { state ->
        binding.news.withModels {
            dividerView {
                id("dividerId")
            }
            state.chats.forEach { chat ->
                chatsView {
                    id(chat.id)
                    chat(chat)
                    actionNavToChat { _ ->
                        viewModel.navToChat(chat.id)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.add_chat, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED,
        )
    }

    override fun onResume() {
        super.onResume()
        if (activity is ActionBarTitleChanger) {
            val actionBarTitleChanger = activity as? ActionBarTitleChanger
            actionBarTitleChanger?.changeTitle(R.string.chats)
        }
    }
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class ChatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by viewBinding(ItemChatsBinding::bind)

    init {
        inflate(context, R.layout.item_chats, this)
    }

    @ModelProp
    fun setChat(performance: Chat) {
        binding.speakerName.text = performance.name
        binding.lastMessage.text = Instant.ofEpochMilli(performance.date).convertTimeTo()
        binding.imageChat.loadWithCircle("https://masterpiecer-images.s3.yandex.net/8d73ce257d8611eea814d659965eed18:upscaled")
    }

    @CallbackProp
    fun actionNavToChat(onClickListener: OnClickListener?) =
        binding.root.onClickWithDebounce(onClickListener)
}
