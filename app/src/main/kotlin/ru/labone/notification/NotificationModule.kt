package ru.labone.notification

import android.app.Application
import com.jenzz.appstate.AppState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asFlow
import ru.labone.AppScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun notificationService(
        app: Application,
        appState: Observable<AppState>,
        appScope: AppScope,
    ): NotificationService = NotificationServiceImpl(app, appScope, appState.asFlow())
}
