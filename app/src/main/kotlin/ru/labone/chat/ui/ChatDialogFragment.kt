package ru.labone.chat.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.VisibilityState
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
import ru.labone.chats.data.ChatMessage
import ru.labone.chats.data.OtherUserMessage
import ru.labone.chats.data.Status
import ru.labone.chats.data.UserMessage
import ru.labone.doAfterTextChanged
import ru.labone.fullScreenDialog
import ru.labone.loadWithCircle
import ru.labone.navigateBack
import ru.labone.news.ui.dividerView
import ru.labone.onClickWithDebounce
import ru.labone.ui
import ru.labone.viewbinding.viewBinding
import splitties.resources.colorSL
import splitties.resources.drawable
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.max


class ChatDialogFragment : DialogFragment(R.layout.fragment_chat), MavericksView {

    private val viewModel by fragmentViewModel(ChatViewModel::class)
    private val binding by viewBinding(FragmentChatBinding::bind)
    private val args: ChatDialogFragmentArgs by navArgs()
    private var verticalScrollOffset = AtomicInteger(0)

    private val layoutChangeListener =
        View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            val y = oldBottom - bottom
            if (abs(y) > 0 && isAdded) {
                binding.messages.post {
                    if (y > 0 || abs(verticalScrollOffset.get()) >= abs(y)) {
                        ui { binding.messages.scrollBy(0, y) }
                    } else {
                        ui { binding.messages.scrollBy(0, verticalScrollOffset.get()) }
                    }
                }
            }
        }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        var state = AtomicInteger(RecyclerView.SCROLL_STATE_IDLE)

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            state.compareAndSet(RecyclerView.SCROLL_STATE_IDLE, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    if (!state.compareAndSet(RecyclerView.SCROLL_STATE_SETTLING, newState)) {
                        state.compareAndSet(RecyclerView.SCROLL_STATE_DRAGGING, newState)
                    }
                }

                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    state.compareAndSet(RecyclerView.SCROLL_STATE_IDLE, newState)
                }

                RecyclerView.SCROLL_STATE_SETTLING -> {
                    state.compareAndSet(RecyclerView.SCROLL_STATE_DRAGGING, newState)
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (state.get() != RecyclerView.SCROLL_STATE_IDLE) {
                verticalScrollOffset.getAndAdd(dy)
            }
        }
    }

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
        val chat = state.chat
        val messages = state.messages
        if (state.message != binding.cetMessage.text.toString()) {
            binding.cetMessage.setTextKeepState(state.message)
        }
        binding.sendMessage.isEnabled = state.message.isNotBlank()
        binding.addChatTab.speakerName.text = chat?.name
        binding.addChatTab.imageChat.loadWithCircle("https://masterpiecer-images.s3.yandex.net/8d73ce257d8611eea814d659965eed18:upscaled")
        binding.messages.withModels {
            dividerView {
                id("dividerId")
            }
            messages
                .forEach { (date, messages) ->
                    dateView {
                        id(date.toEpochDay())
                        date(date)
                    }
                    val readMessages = messages.filter { it.readDate != null || it is UserMessage }
                    val unReadMessages = messages.filterIsInstance<OtherUserMessage>().filter { it.readDate == null }
                    readMessages.forEach { message ->
                        when (message) {
                            is OtherUserMessage -> {
                                otherUserMessageView {
                                    id(message.id)
                                    message(message)
                                    authorName(message.authorName)
                                    authorAvatar(message.authorAvatar)
                                }
                            }

                            is UserMessage -> userMessageView {
                                id(message.id)
                                message(message)
                            }
                        }
                    }
                    if (unReadMessages.isNotEmpty()) {
                        unReadMessageView {
                            id("unreadMessage")
                        }
                    }
                    unReadMessages.forEach { message ->
                        otherUserMessageView {
                            id(message.id)
                            message(message)
                            authorName(message.authorName)
                            authorAvatar(message.authorAvatar)
                            onVisibilityStateChanged { _, _, visibilityState ->
                                if (visibilityState == VisibilityState.VISIBLE) {
                                    viewModel.markRead(date, message.id)
                                }
                            }
                        }
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addChatTab.navigateBack.setOnClickListener { navigateBack() }
        setupRecyclerView()
        binding.cetMessage.doAfterTextChanged {
            viewModel.changeMessage(it.toString())
        }

        binding.sendMessage.onClickWithDebounce {
            viewModel.saveMessage()
        }
        viewModel.effect.collect(lifecycleScope) { effect ->
            when (effect) {
                is ScrollToNextPosition -> {
                    val layoutManager = binding.messages.layoutManager as? LinearLayoutManager
                    binding.messages.postDelayed(50L) {
                        layoutManager?.scrollToPositionWithOffset(effect.index, 0)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        EpoxyVisibilityTracker().attach(binding.messages)
        val layoutManager = binding.messages.layoutManager as? LinearLayoutManager
        binding.messages.addOnScrollListener(onScrollListener)
        binding.messages.addOnLayoutChangeListener(layoutChangeListener)
        binding.messages.withModels {
            var scrollPos = -1
            adapter.addModelBuildListener {
                if (scrollPos >= 0) {
                    layoutManager?.apply {
                        viewModel.scrollToPosition(scrollPos)
                    }
                    scrollPos = -1
                }
            }
            adapter.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        scrollPos = max(scrollPos, positionStart + itemCount - 1)
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        if (adapter.itemCount == positionStart + itemCount) {
                            scrollPos = max(scrollPos, adapter.itemCount - 1)
                        }
                    }
                }
            )
        }
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
internal class UnReadMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.item_unread_message, this)
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
    fun setMessage(message: UserMessage) {
        binding.message.text = message.text?.trim()
        binding.messageDate.text = Convertors.temporalToUITime(message.createDate)
        binding.status.imageTintList = colorSL(
            when {
                message.readDate != null -> R.color.icon_color
                message.status == Status.SENDING -> R.color.gray
                message.status == Status.NOT_SEND -> R.color.colorError
                else -> R.color.icon_color
            }
        )
        binding.status.setImageDrawable(
            drawable(
                when {
                    message.readDate != null -> R.drawable.ic_double_check
                    message.status == Status.SENDING -> R.drawable.ic_clock
                    message.status == Status.NOT_SEND -> R.drawable.ic_error
                    else -> R.drawable.ic_check
                }
            )
        )
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
    fun setAuthorAvatar(avatar: String?) {
        binding.personImage.visibility = if (avatar != null) VISIBLE else INVISIBLE
        binding.personImage.loadWithCircle("https://cs14.pikabu.ru/post_img/big/2023/02/13/8/1676295806139337963.png")
    }
}
