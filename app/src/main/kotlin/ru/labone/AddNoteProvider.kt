package ru.labone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.ActionProvider
import com.example.labone.databinding.AddNoteActionBinding
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.labone.addnote.data.AddNewsNavKey
import ru.labone.naviagion.Navigator

class AddNoteActionProvider(context: Context) : ActionProvider(context) {
    private val navigator: Navigator
    private val binding: AddNoteActionBinding =
        AddNoteActionBinding.inflate(LayoutInflater.from(context))

    init {
        val entryPoint = EntryPoints.get(context.applicationContext, FilterAPEntryPoint::class.java)
        navigator = entryPoint.navigatorPublish()
    }

    override fun onCreateActionView(): View = binding.root
        .apply {
            onClickWithDebounce {
                navigator.navigateTo(AddNewsNavKey())
            }
        }
}

@InstallIn(SingletonComponent::class)
@EntryPoint
internal interface FilterAPEntryPoint {
    fun navigatorPublish(): Navigator
}
