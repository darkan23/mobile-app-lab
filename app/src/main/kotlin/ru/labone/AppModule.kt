package ru.labone

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import ru.labone.filemanager.FileManager
import ru.labone.filemanager.FileManagerImpl
import ru.labone.naviagion.Navigator
import ru.labone.naviagion.NavigatorCollect
import ru.labone.naviagion.NavigatorImpl
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val PING_INTERVAL = 30L
const val CONNECT_TIME_OUT = 30L
const val WRITE_AND_READ_INTERVAL = 60L

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun fileCreator(application: Application): FileManager = FileManagerImpl(application)

    @Provides
    @Singleton
    fun applicationCoroutineScope(): AppScope = AppScope()

    @Provides
    @Singleton
    fun provideNavigatorNav(
        appScope: AppScope,
        application: Application,
    ): Navigator = NavigatorImpl(
        appScope,
        application,
    )

    @Provides
    @Singleton
    fun provideNavigatorNav1(navigator: Navigator): NavigatorCollect = navigator as NavigatorCollect

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient = Builder()
        .apply {
            authenticator { _, _ ->
                null
            }
            addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                val reqBuilder = request.newBuilder()
                chain.proceed(reqBuilder.build())
            }
        }
        .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .pingInterval(PING_INTERVAL, TimeUnit.SECONDS)
        .readTimeout(WRITE_AND_READ_INTERVAL, TimeUnit.SECONDS)
        .writeTimeout(WRITE_AND_READ_INTERVAL, TimeUnit.SECONDS)
        .build()
}
