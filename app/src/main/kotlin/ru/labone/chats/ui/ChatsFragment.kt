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
import androidx.core.view.isVisible
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
import ru.labone.chats.data.OtherUserMessage
import ru.labone.chats.data.Status
import ru.labone.chats.data.UserMessage
import ru.labone.loadWithCircle
import ru.labone.news.ui.dividerView
import ru.labone.onClickWithDebounce
import ru.labone.viewbinding.viewBinding
import splitties.resources.colorSL
import splitties.resources.drawable

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
    fun setChat(chat: UiChat) {
        val messages = chat.messages
        val lastMessage = messages.lastOrNull()
        val userMessages = lastMessage is UserMessage
        val otherUserMessages = messages.filterIsInstance<OtherUserMessage>().filter { it.readDate == null }
        println("FUCK ${lastMessage}")
        binding.unreadMessageCount.isVisible = otherUserMessages.isNotEmpty()
        binding.status.isVisible = userMessages
        if (lastMessage is UserMessage) {
            binding.status.imageTintList = colorSL(
                when {
                    lastMessage.readDate != null -> R.color.icon_color
                    lastMessage.status == Status.SENDING -> R.color.gray
                    lastMessage.status == Status.NOT_SEND -> R.color.colorError
                    else -> R.color.icon_color
                }
            )
            binding.status.setImageDrawable(
                drawable(
                    when {
                        lastMessage.readDate != null -> R.drawable.ic_double_check
                        lastMessage.status == Status.SENDING -> R.drawable.ic_clock
                        lastMessage.status == Status.NOT_SEND -> R.drawable.ic_error
                        else -> R.drawable.ic_check
                    }
                )
            )
        } else {
            binding.unreadMessageCount.text = otherUserMessages.size.toString()
        }
        binding.speakerName.text = chat.name
        binding.lastMessage.text = lastMessage?.text
        binding.imageChat.loadWithCircle("https://masterpiecer-images.s3.yandex.net/8d73ce257d8611eea814d659965eed18:upscaled")
    }

    @CallbackProp
    fun actionNavToChat(onClickListener: OnClickListener?) =
        binding.root.onClickWithDebounce(onClickListener)
}
