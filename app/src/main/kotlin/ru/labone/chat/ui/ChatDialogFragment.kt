package ru.labone.chat.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.example.labone.R
import com.example.labone.databinding.FragmentChatBinding
import com.example.labone.databinding.ItemChatOtherUserMessageBinding
import com.example.labone.databinding.ItemChatUserMessageBinding
import com.example.labone.databinding.ItemMessageDateBinding
import ru.labone.Convertors
import ru.labone.chat.data.takeAuthorAvatar
import ru.labone.chat.data.takeAuthorName
import ru.labone.chats.data.ChatMessage
import ru.labone.chats.data.OtherUserMessage
import ru.labone.chats.data.UserMessage
import ru.labone.fullScreenDialog
import ru.labone.loadWithCircle
import ru.labone.navigateBack
import ru.labone.news.ui.dividerView
import ru.labone.viewbinding.viewBinding
import java.time.LocalDate

class ChatDialogFragment : DialogFragment(R.layout.fragment_chat), MavericksView {

    private val viewModel by fragmentViewModel(ChatViewModel::class)
    private val binding by viewBinding(FragmentChatBinding::bind)
    private val args: ChatDialogFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (args.chatNavKey != null) {
            arguments = Bundle().apply {
                putParcelable(Mavericks.KEY_ARG, args.chatNavKey)
            }
        }
        super.onCreate(savedInstanceState)
        fullScreenDialog()
    }

    override fun invalidate() = withState(viewModel) { state ->
        val chat = state.messages.first
        val messages = state.messages.second
        binding.addChatTab.title = chat.name
        binding.messages.withModels {
            dividerView {
                id("dividerId")
            }
            messages.forEach { (date, messages) ->
                dateView {
                    id(date.toEpochDay())
                    date(date)
                }
                messages.forEachIndexed { index, message ->
                    val previousMessage = messages.getOrNull(index - 1)
                    val nextMessage = messages.getOrNull(index + 1)
                    when (message) {
                        is OtherUserMessage -> {
                            val currentAuthorId = message.author.id
                            val previousAuthorId =
                                (previousMessage as? OtherUserMessage)?.author?.id
                            val nextAuthorId = (nextMessage as? OtherUserMessage)?.author?.id
                            val authorAvatar =
                                message.takeAuthorAvatar(currentAuthorId, nextAuthorId)?.photoId
                            val authorName =
                                message.takeAuthorName(currentAuthorId, previousAuthorId)?.name
                            otherUserMessageView {
                                id(message.id)
                                message(message)
                                authorName(authorName)
                                authorAvatar(authorAvatar)
                            }
                            if (currentAuthorId != nextAuthorId) {
                                dividerView {
                                    id("dividerMessage$index")
                                }
                            }
                        }

                        is UserMessage -> {
                            userMessageView {
                                id(message.id)
                                message(message)
                            }
                            if (nextMessage is OtherUserMessage || nextMessage == null) {
                                dividerView {
                                    id("dividerMessage$index")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addChatTab.setNavigationOnClickListener { navigateBack() }
    }
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class DateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by viewBinding(ItemMessageDateBinding::bind)

    init {
        inflate(context, R.layout.item_message_date, this)
    }

    @ModelProp
    fun setDate(date: LocalDate) {
        val currentDate = LocalDate.now()
        val hasYear = date.year != currentDate.year
        binding.date.text = if (hasYear) {
            Convertors.temporalToUIFullYear(date)
        } else {
            Convertors.temporalToUIYearMonth(date)
        }
    }
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class UserMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by viewBinding(ItemChatUserMessageBinding::bind)

    init {
        inflate(context, R.layout.item_chat_user_message, this)
    }

    @ModelProp
    fun setMessage(message: ChatMessage) {
        binding.message.text = message.text
        binding.messageDate.text = Convertors.temporalToUITime(message.createDate)
    }
}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
internal class OtherUserMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by viewBinding(ItemChatOtherUserMessageBinding::bind)

    init {
        inflate(context, R.layout.item_chat_other_user_message, this)
    }

    @ModelProp
    fun setMessage(message: ChatMessage) {
        binding.message.text = message.text
        binding.messageDate.text = Convertors.temporalToUITime(message.createDate)
    }

    @ModelProp
    fun setAuthorName(name: String?) {
        binding.personName.isVisible = !name.isNullOrBlank()
        binding.personName.text = name
    }

    @ModelProp
    fun setAuthorAvatar(avatar: Long?) {
        binding.personImage.visibility = if (avatar != null) VISIBLE else INVISIBLE
        binding.personImage.loadWithCircle("https://cs14.pikabu.ru/post_img/big/2023/02/13/8/1676295806139337963.png")
    }
}
