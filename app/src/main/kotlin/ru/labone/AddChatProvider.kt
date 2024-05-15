package ru.labone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ActionProvider
import com.example.labone.databinding.AddChatActionBinding
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.labone.addchat.data.AddChatNavKey
import ru.labone.naviagion.Navigator

class AddChatProvider(context: Context) : ActionProvider(context) {
    private val navigator: Navigator
    private val binding: AddChatActionBinding = AddChatActionBinding.inflate(LayoutInflater.from(context))

    init {
        val entryPoint = EntryPoints.get(context.applicationContext, AddChatAPEntryPoint::class.java)
        navigator = entryPoint.navigatorPublish()
    }

    override fun onCreateActionView(): View = binding.root
        .apply {
            onClickWithDebounce {
                navigator.navigateTo(AddChatNavKey())
            }
        }
}

@InstallIn(SingletonComponent::class)
@EntryPoint
internal interface AddChatAPEntryPoint {
    fun navigatorPublish(): Navigator
}
