package ru.labone.chats.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ru.labone.AppScope
import ru.labone.chat.data.ChatGPTApi
import ru.labone.chats.ui.ChatsViewModel
import ru.labone.mvrx.AssistedViewModelFactory
import ru.labone.mvrx.ViewModelKey
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ChatsModule {

    @Provides
    @Singleton
    internal fun provideChatsRepo(
        appScope: AppScope,
        chatDao: ChatDao,
        gptApi: ChatGPTApi,
    ): ChatsRepository = ChatsRepositoryImpl(appScope, chatDao, gptApi)

    @Provides
    @IntoMap
    @ViewModelKey(ChatsViewModel::class)
    fun viewModelFactory(actual: ChatsViewModel.Factory): AssistedViewModelFactory<*, *> = actual

    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    @Provides
    fun provideApiGPTNetworkRequest(okHttpClient: OkHttpClient): ChatGPTApi =
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ChatGPTApi::class.java)
}
